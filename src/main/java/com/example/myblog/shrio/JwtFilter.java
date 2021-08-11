package com.example.myblog.shrio;

import cn.hutool.json.JSONUtil;
import com.example.myblog.Util.JwtUtils;
import com.example.myblog.lang.Result;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends AuthenticatingFilter {


//    @Autowired
//    JwtUtils jwtUtils;
//
//
//    @Override
//    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
//        HttpServletRequest request= (HttpServletRequest) servletRequest;
//
//        //查找请求头，里面是否有Authorization的token
//        //后续还要进行判断TOken是否有过期或者不对的情况
//        String jwt=request.getHeader("Authorization");
//        if(StringUtils.checkValNotNull(jwt)){
//            return null;
//        }
//        return new JwtToken(jwt);
//    }
//
//
//    //这个方法是进行判断token是否正确，是否存在过期的情况
//    @Override
//    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
//
//
//
//        HttpServletRequest request= (HttpServletRequest) servletRequest;
//        //获取到token
//        String jwt=request.getHeader("Authorization");
//        if(StringUtils.checkValNotNull(jwt)){
//            return true;
//        }else {
//
//            // 校验token
//            Claims claims=jwtUtils.getClaimByToken(jwt);
//            // 如果为空，或者过期抛出异常
//            if (claims==null||jwtUtils.isTokenExpired(claims.getExpiration())){
//                throw new ExpiredCredentialsException("token过期，重新登录");
//            }
//            // 执行登录
//            return executeLogin(servletRequest,servletResponse);
//        }
//    }
//
//    @Override
//    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
//
//        HttpServletResponse servletResponseresponse= (HttpServletResponse) response;
//
//        Throwable throwable = e.getCause() == null ? e : e.getCause();
//
//        //token错误后，返回错误信息给前端
//       Result result= Result.fail(throwable.getMessage());
//
//        // 转成json
//       String json= JSONUtil.toJsonStr(result);
//
//        try {
//            // 返回给前端
//            servletResponseresponse.getWriter().print(json);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }

    @Autowired
    JwtUtils jwtUtils;
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // 获取 token
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String jwt = request.getHeader("Authorization");
        if(StringUtils.isEmpty(jwt)){
            return null;
        }
        return new JwtToken(jwt);
    }
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader("Authorization");
        if(StringUtils.isEmpty(token)) {
            return true;
        } else {
            // 判断是否已过期
            Claims claim = jwtUtils.getClaimByToken(token);
            if(claim == null || jwtUtils.isTokenExpired(claim.getExpiration())) {
                throw new ExpiredCredentialsException("token已失效，请重新登录！");
            }
        }
        // 执行自动登录
        return executeLogin(servletRequest, servletResponse);
    }
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            //处理登录失败的异常
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            Result r = Result.fail(throwable.getMessage());
            String json = JSONUtil.toJsonStr(r);
            httpResponse.getWriter().print(json);
        } catch (IOException e1) {
        }
        return false;
    }
    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
