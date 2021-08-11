package com.example.myblog.Controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myblog.Util.JwtUtils;
import com.example.myblog.dto.LoginDTO;
import com.example.myblog.entity.User;
import com.example.myblog.lang.Result;
import com.example.myblog.service.UserService;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("login")
    public Result login(@Validated @RequestBody LoginDTO loginDTO, HttpServletResponse response){

        // 这里是通过userservice查询用户名是否有在里面,着是mybatis plus的方法QueryWrapper
      User user=  userService.getOne(new QueryWrapper<User>().eq("username",loginDTO.getUsername()));

       // 如果为空，返回一个非法参数异常
        Assert.notNull(user,"用户不存在");

        // 判断密码是否正确，如果不正确，返回密码错误
        if(!user.getPassword().equals(SecureUtil.md5(loginDTO.getPassword()))){
            return Result.fail("密码不正确");
        }

        // 密码正确，写tooken,通过ID生成令牌
       String jwt=  jwtUtils.generateToken(user.getId());

        // Authorization授权
        response.setHeader("Authorization",jwt);
        response.setHeader("Access-control-Expose-headers","Authorization");


        return Result.succ(MapUtil.builder().put("id",user.getId())
                .put("username",user.getUsername())
                .put("avatar",user.getAvatar())
                .put("id",user.getId())
                .put("email",user.getEmail()).map()
        );
    }


    @RequiresAuthentication
    @PostMapping("/logout")
    public Result logout(){

        // shiro自带的logout方法
        SecurityUtils.getSubject().logout();
        return Result.succ(null);
    }
}
