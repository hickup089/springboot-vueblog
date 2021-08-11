package com.example.myblog.shrio;

import com.example.myblog.Util.JwtUtils;
import com.example.myblog.entity.User;
import com.example.myblog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountRealm extends AuthorizingRealm {

    @Autowired
    JwtUtils jwtUtils;


    @Autowired
    UserService userService;


    @Override
    public boolean supports(AuthenticationToken token) {
        // 3这里是要判断token是否是JWT的Token，如果不是，则不会进行下面
        return token instanceof JwtToken;
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {


        return null;
    }
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
         // 这里的token必须要进行强转，有判断强转的方法，在上面3处
        JwtToken jwtToken= (JwtToken) token;

        //获取token
        String userid=jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
       User user= userService.getById(Long.valueOf(userid));

       if(user==null){
           // 如果user为空，则账户不存在
           throw new UnknownAccountException("账户不存在");
       }
       // -1状态为锁定
       if (user.getStatus()==-1){
//           返回账户锁定，这些异常都是写好了的
            throw new LockedAccountException("账户已被锁定");
       }

//        JwtToken jwt = (JwtToken) token;
//        log.info("jwt----------------->{}", jwt);
//        String userId = jwtUtils.getClaimByToken((String) jwt.getPrincipal()).getSubject();
//        User user = userService.getById(Long.parseLong(userId));
//        if(user == null) {
//            throw new UnknownAccountException("账户不存在！");
//        }
//        if(user.getStatus() == -1) {
//            throw new LockedAccountException("账户已被锁定！");
//        }
//        AccountProfile profile = new AccountProfile();
//        BeanUtil.copyProperties(user, profile);
//        log.info("profile----------------->{}", profile.toString());
//        return new SimpleAuthenticationInfo(profile, jwt.getCredentials(), getName());

        AccountProfile accountProfile=new AccountProfile();
        BeanUtils.copyProperties(user,accountProfile);
        return new SimpleAuthenticationInfo(accountProfile,jwtToken.getCredentials(),getName());
    }
}
