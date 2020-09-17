package com.hxz.test.login;

import com.hxz.test.login.menu.pojo.SysMenuInfo;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.attoparser.util.TextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.TextUtils;

import javax.servlet.http.HttpSession;
import java.util.*;

@ServletComponentScan  // 打包 war 时  需要用到这个注解 并继承自SpringBootServletInitializer  jar 包 不需要
@EnableAsync
@Slf4j
@SpringBootApplication
public class LoginApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(LoginApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
    }

}

@Slf4j
@Controller
@RequestMapping("/")
@Configuration
class IndexControlView {

    @GetMapping("index")
    public ModelAndView index(HttpSession session) {
//        log.error("index::::" + session.getAttribute("login").toString());
        ModelAndView modelAndView = new ModelAndView("index");

        modelAndView.addObject("menus",getMenu());

        return modelAndView;
    }

    private SysMenuInfo[] getMenu() {
        SysMenuInfo[] menus = new SysMenuInfo[4];
        for (int i = 0;i < 4;i++) {
            SysMenuInfo menuInfo = new SysMenuInfo();
            menuInfo.setName("菜单" + i);
            menuInfo.setSysPath("");
            menuInfo.setMenuId("");
            List<SysMenuInfo> childrens = new ArrayList<>();
            for (int j = 0;j < 2;j++) {
                SysMenuInfo sysMenuInfo = new SysMenuInfo();
                sysMenuInfo.setName("子菜单" + j);
                sysMenuInfo.setMenuId("");
                sysMenuInfo.setSysPath("");
                childrens.add(sysMenuInfo);
            }
            menuInfo.setChildren(childrens);
            menus[i] = menuInfo;
        }
        return menus;
    }

    @GetMapping("logout")
    public ModelAndView logout(HttpSession session) {
        session.removeAttribute("login");
        session.invalidate();
        ModelAndView mv = new ModelAndView();
        mv.setViewName("forward:login");
        return mv;
    }

    @GetMapping("login")
    public ModelAndView login(HttpSession session) {
        Object login = session.getAttribute("login");
        ModelAndView modelAndView = new ModelAndView();
        if (login != null) {
            modelAndView.setViewName("forward:index");
        } else {
            modelAndView.setViewName("login");
            modelAndView.addObject("test","登录测试");
        }
        return modelAndView;
    }

    @GetMapping("test")
    public ModelAndView test() {
        return new ModelAndView("sys/test");
    }

    @GetMapping("sys/test")
    public ModelAndView sysTest(){
        return new ModelAndView("sys/test");
    }

    @GetMapping("sys/editor")
    public ModelAndView sysEditor() {
        return new ModelAndView("sys/editor");
    }

    @GetMapping("sys/upload")
    public ModelAndView sysUpload() {
        return new ModelAndView("sys/upload");
    }

    @GetMapping("sys/userInfo")
    public ModelAndView sysUserInfo() {
        return new ModelAndView("sys/userInfo");
    }
}
