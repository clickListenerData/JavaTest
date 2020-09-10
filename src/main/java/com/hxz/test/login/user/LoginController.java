package com.hxz.test.login.user;

import com.hxz.test.login.bean.Login;
import com.hxz.test.login.common.Result;
import com.hxz.test.login.user.service.UserService;
import com.hxz.test.login.user.service.UserServiceImpl;
import com.hxz.test.login.user.vo.UserVo;
import com.hxz.test.login.utils.JwtSignUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/")
@Slf4j
public class LoginController {

    private static String UPLOAD_PATH = "File/txt/upload";

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceBundle bundle;

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public Result<Login> login(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        // sql  user pwd  userId
        // userId token
        // ?token
        UserVo userVo = userService.findByUserName(userName);
        if (userVo.getUserId() == null) {
            return Result.custom(100,"用户不存在",null);
        }else {
            if (userVo.getPassword().equals(password)) {
                return Result.success(JwtSignUtils.sign(userVo.getUserId()));
            } else {
                return Result.custom(101, "密码错误", null);
            }
        }
    }

    @PostMapping("register")
    public Result<UserVo> register(String userName,String password) {
        UserVo user = userService.findByUserName(userName);
        if (user.getUserId() != null) {
            return Result.custom(100,"用户已存在",null);
        }
        UserVo userVo = new UserVo(userName,password);
        return userService.save(userVo);
    }

    @PostMapping("get_user")
    public Result<UserVo> getUser(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (JwtSignUtils.verify(token)) {
            String userId = JwtSignUtils.getUserId(token);
            return userService.get(userId);
        } else  {
            return Result.custom(102,"token error",null);
        }
    }

    @PostMapping("refresh")
    public Result<Login> refresh(HttpServletRequest request) {
        // web socket 长链接    再 重新登录 时  通知以前的  失效  并 断开 以前的链接
        String token = request.getHeader("token");
        if (JwtSignUtils.verify(token)) {
            String userId = JwtSignUtils.getUserId(token);
            return Result.success(JwtSignUtils.sign(userId));  // 续签
        } else {
            return Result.custom(103,"token error",null);
        }
    }

    @PostMapping("change_password")
    public Result<UserVo> changePassword(HttpServletRequest request,@RequestParam String newPassword) {
        String token = request.getHeader("token");
        if (JwtSignUtils.verify(token)) {
            String userId = JwtSignUtils.getUserId(token);
            UserVo userVo = userService.changePassword(userId, newPassword);
            return Result.success(userVo);
        } else  {
            return Result.custom(102,"token error",null);
        }
    }

    @PostMapping("verify")
    public Result<Boolean> verify(String token) {
        return Result.success(JwtSignUtils.verify(token));
    }

    @GetMapping("test")
    public Result<String> test(HttpServletResponse response) {
//        response.addHeader("token","123456");
//        Result<List<UserVo>> list = userService.list();
//        return list;
        throw new RuntimeException("custom error");
        /*String s = "";
        try {
            byte[] bytes = bundle.getString("hello").getBytes("ISO-8859-1");
            s = new String(bytes,"UTF-8");  // 中文乱码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Result.success(s);*/
       /* HashMap<String,Object> map = new HashMap();
        map.put("name","熊昌");
        map.put("age",10);
        return Result.custom(200,"成功",map);*/
    }

    @PostMapping("fileUpload")  // param ： 默认为 文件名
    public Result<String> fileUpload(@RequestParam("test.txt") MultipartFile file) {
        if (file == null) {
            return Result.error("");
        }
        long startTime = System.currentTimeMillis();
        String path = "D:/" + file.getOriginalFilename();
        try {
            file.transferTo(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.error("上传文件：：" + (System.currentTimeMillis() - startTime));
        return Result.success("上传成功");
    }

    @PostMapping("upload")
    public Result<String> uploadFile(HttpServletRequest request) {
        log.error(request.getHeader("Content-Length"));
        long startTime = System.currentTimeMillis();
        CommonsMultipartResolver resolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        // 检查 form 中 是否有enctype = "multipart/form-data"
        if (resolver.isMultipart(request)) {
            // 变成 多部分 request
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            // 获取request 中所有的文件名
            Iterator<String> iterator = multipartHttpServletRequest.getFileNames();
            while (iterator.hasNext()) {
                MultipartFile file = multipartHttpServletRequest.getFile(iterator.next().toString());
                if (file != null) {
                    /*String path = "D:/" + file.getOriginalFilename();
                    try {
                        file.transferTo(new File(path));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    String fileDir = request.getSession().getServletContext().getRealPath("fileDir");
                    File dirFile = new File(fileDir);
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                    File saveFile = new File(fileDir,file.getOriginalFilename());
                    try {
                        file.transferTo(saveFile);
//                        String replace = request.getRequestURL().toString().replace("/upload","");
                        // replace: http://localhost:8080/login  或者  http://localhost:1111
                        // replace + "/" + "fileDir" + "/" + "test.txt"
                        // http://localhost:1111/fileDir/test.txt
                        log.error(saveFile.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.error("上传耗时：：" + (System.currentTimeMillis() - startTime));
            /*Map<String, MultipartFile> fileMap = multipartHttpServletRequest.getFileMap();
            for (Map.Entry<String,MultipartFile> entry : fileMap.entrySet()) {
                log.error(entry.getKey() + "::" + entry.getValue().getName());
            }*/
            return Result.custom(100,"成功","data");
        }
        return Result.error("");
    }

    private String uploadFile(MultipartFile file) throws Exception {
        String name = file.getOriginalFilename();
        // 目录路径
        Path directory = Paths.get(UPLOAD_PATH);
        // 判断目录是否存在，不存在创建
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        // 判断文件是否存在,存在删除
        if (Files.exists(directory.resolve(name))) {
            File newFile = new java.io.File(UPLOAD_PATH + "/" + name);
            newFile.delete();
        }
        // 拷贝文件
        Files.copy(file.getInputStream(), directory.resolve(name));
        return "/" + UPLOAD_PATH + "/" + name;
    }


    @PostMapping("chunk/file")
    public Result<String> uploadChunkFile(@RequestParam("file") MultipartFile file,@RequestParam("chunks") long chunks,@RequestParam("chunk") long chunk,
                                          HttpServletRequest request,HttpServletResponse response) {
        log.error(chunks + ":::" + chunk);
        String fileDir = request.getSession().getServletContext().getRealPath("fileDir");  // 上传文件的目录
        File dirFile = new File(fileDir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();  // 创建目录
        }

        String fileName = file.getOriginalFilename();
        RandomAccessFile raf = null;
        InputStream is = null;
        try {
            raf = new RandomAccessFile(new File(fileDir,fileName),"rwd");
            if (chunk != 0) raf.seek(chunk);
            is = file.getInputStream();
            int len = 0;
            byte[] bytes = new byte[2048];
            while((len = is.read(bytes)) != -1) {
                raf.write(bytes,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.error(dirFile.getAbsolutePath());
        return Result.success(dirFile.getAbsolutePath());
    }

    @PostMapping("chunk/size")
    public Result<String> getChunkSize(HttpServletRequest request,@RequestParam("chunks") long chunks,@RequestParam("name") String fileName) {
        String fileDir = request.getSession().getServletContext().getRealPath("fileDir");  // 上传文件的目录
        File file = new File(fileDir,fileName); // 一般情况下 fileName  为 第一次上传的时候 服务器传回给客户端的名字
        if (file.exists()) {
            return Result.success(file.length() + "");
        } else {
            return Result.error(null);
        }
    }

    @PostMapping("big/file")
    public Result<String> uploadBigFile(@RequestParam("file") MultipartFile file,@RequestParam("chunks") long chunks,@RequestParam("chunk") long chunk,
                                        HttpServletRequest request,HttpServletResponse response) {
        String fileDir = request.getSession().getServletContext().getRealPath("fileDir");  // 上传文件的目录
        File dirFile = new File(fileDir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();  // 创建目录
        }

        String fileName = file.getOriginalFilename();
        // 存储临时文件
        File tempFile = new File(fileDir,chunk + "_" + fileName);
        try {
            file.transferTo(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (chunks == chunk + 1) {  // 所有文件传输完成
            File newFile = new File(fileDir,fileName);
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(newFile));
                for (int i = 0;i < chunks; i++) {
                    File tf = new File(fileDir,i + "_" + fileName);
                    byte[] bytes = FileUtils.readFileToByteArray(tf);
                    bos.write(bytes);
                    bos.flush();
                    tf.delete();  // 删除临时文件
                }
                log.error("upload success");
                bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return Result.success("");
    }

    @PostMapping("big/chunks")
    public Result<Integer> getBigSize(HttpServletRequest request,@RequestParam("chunks") long chunks,@RequestParam("name") String fileName) {
        String fileDir = request.getSession().getServletContext().getRealPath("fileDir");  // 上传文件的目录
        File file = new File(fileDir,fileName); // 一般情况下 fileName  为 第一次上传的时候 服务器传回给客户端的名字
        if (file.exists()) {
            return Result.success((int)chunks);  // 文件已全部上传完
        }
        for (int i = 0;i < chunks; i++) {
            File tempFile = new File(fileDir,i + "_" + fileName);
            if (!tempFile.exists()) {
                return Result.success(i);  // 文件 已上传至 第 i - 1 块  为 0 时 表示文件未上传
            }
        }
        // 此时 还未返回 表示文件已上传完 但还没有合并
        return Result.error(null);
    }

    @GetMapping("get/txt/{name}")
    public void getTxtForName(@PathVariable("name") String name,HttpServletRequest request,HttpServletResponse response) {
        String fileDir = request.getSession().getServletContext().getRealPath("fileDir");
        File file = new File(fileDir,name);
        if (!file.exists()) {
            response.setStatus(400);
        }
        // 开始下载的位置
        long startByte = 0;
        // 结束下载位置
        long endByte = file.length() - 1;
        // 断点 下载的 请求头
        String range = request.getHeader("range");
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

            // 拿到 = 后面的值
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String[] ranges = range.split("-");
            try{
                if (ranges.length == 1) {
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    } else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                } else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }
            }catch (Exception e) {
                startByte = 0;
                endByte = file.length() - 1;
            }
        }
        log.error(startByte + "::" + endByte);

        // 要下载的长度  （endByte 为总长度 - 1 这时候要加回去）
        long contentLength = endByte - startByte + 1;
        String fileName = file.getName();
        // 文件类型
        String contentType = request.getServletContext().getMimeType(fileName);

        //参考资料：https://www.ibm.com/developerworks/cn/java/joy-down/index.html
        response.setHeader("Accept-Ranges","bytes");
        response.setContentType(contentType);
        response.setHeader("Content-Type",contentType);

        response.setHeader("Content-Disposition","inline;filename=file.apk");
        response.setHeader("Content-Length",String.valueOf(contentLength));
        // 下载的开始位置-结束位置/总大小
        response.setHeader("Content-Range","bytes " + startByte + "-" + endByte + "/" + file.length());

        BufferedOutputStream outputStream = null;
        RandomAccessFile randomAccessFile = null;

        // 已传送数据大小
        long transmitted = 0;
        try{
            randomAccessFile = new RandomAccessFile(file,"r");
            outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[4096];
            int len = 0;
            randomAccessFile.seek(startByte);
            while ((transmitted + len) < contentLength && (len = randomAccessFile.read(buff)) != -1) {
                outputStream.write(buff,0,len);
                transmitted += len;
            }
            // 处理不足4096的部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff,0, (int) (contentLength - transmitted));
                outputStream.write(buff,0,len);
                transmitted += len;
            }
            outputStream.flush();
            response.flushBuffer();
            randomAccessFile.close();
            log.error("download success");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @PostMapping("sendMail")
    public Result<String> sendMail() {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setFrom("huaixianzhong1314@163.com");
            mimeMessageHelper.setTo("772133816@qq.com");
            mimeMessageHelper.setSubject("test test");
            mimeMessageHelper.setText("正文  1234!!!");
            mimeMessageHelper.addInline("test.png",new File("D:/test.png"));
            mimeMessageHelper.addAttachment("test.p12",new File("D:/test.p12"));
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return Result.success("1234");
    }



}
