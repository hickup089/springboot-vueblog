package com.example.myblog.Controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myblog.entity.Blog;
import com.example.myblog.lang.Result;
import com.example.myblog.service.BlogService;
import com.example.myblog.shrio.ShiroUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 关注公众号：MarkerHub
 * @since 2021-08-10
 */
@RestController
public class BlogController {


    @Autowired
    BlogService blogService;

    @GetMapping("/blogs")
    public Result list(@RequestParam(defaultValue = "1") Integer currentPage){

        // mybatisPlus的翻页方法，这里两个参数，页数，每页几条
        Page page=new Page(currentPage,5);

      IPage  ipagedate =blogService.page(page,new QueryWrapper<Blog>().orderByAsc("created"));
        return Result.succ(ipagedate);
    }

    @GetMapping("/blogs/{id}")
    public Result detail(@PathVariable(name = "id") Long id){
        Blog blog=blogService.getById(id);

        // 判断是否为空
        Assert.notNull(blog,"改博客已被删除");

        return Result.succ(blog);
    }

    @RequiresAuthentication
    @PostMapping("/blogs/edit")
    public Result edit(@Validated @RequestBody Blog blog){

        Blog temp=null;
        // 如果传过来的blog有id，说明是编辑状态，如果没有ID则是新增状态
        if (blog.getId()!=null){
            temp=blogService.getById(blog.getId());
            //编辑只能编辑自己的文章
            // 通过用户ID判断和blog的用户ID是否一致，否则不能改动
            Assert.isTrue(temp.getUserId().longValue()== ShiroUtil.getProfile().getId().longValue(),"你没有权限编辑");
        }else {
            temp=new Blog();
            temp.setUserId(ShiroUtil.getProfile().getId());
            temp.setCreated(LocalDateTime.now());
            temp.setStatus(0);

        }

        // 这个方法是对比两个数据，忽略id，userid，create
        BeanUtil.copyProperties(blog,temp,"id","userId","created","status");

        blogService.saveOrUpdate(temp);
        return Result.succ("成功");
    }
}
