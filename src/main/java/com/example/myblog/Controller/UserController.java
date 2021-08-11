package com.example.myblog.Controller;


import com.example.myblog.entity.User;
import com.example.myblog.lang.Result;
import com.example.myblog.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注公众号：MarkerHub
 * @since 2021-08-10
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;


    @RequiresAuthentication
    @GetMapping("/index")
    public Result index(){
        User user=userService.getById(1L);
        return Result.succ("ok",user);
    }

    //@Validated可以通过entity里面校验user是否为空 @RequestBody这个会把传来的json转成entity
    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user){
        System.out.print("ehterer");
        return Result.succ("ok",user);
    }

    @RequestMapping("/getID")
    public User GetID(long id){
     return  userService.getById(id);
    }
}
