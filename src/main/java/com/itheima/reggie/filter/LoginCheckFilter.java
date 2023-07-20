package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER= new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        // 获取本次请求的uri
        String requestURI = request.getRequestURI();
        log.info("拦截的请求：{}",requestURI);
        // 定义不需要处理的uri路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //
        boolean check = check(urls, requestURI);
        // 如果不需要处理，直接放行
        if(check){
            log.info("不需要处理的请求：{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        // 判断登录状态，如果已登录，直接放行
        if(request.getSession().getAttribute("employee") != null){
            Long empId = (Long)request.getSession().getAttribute("employee");
            log.info("已登录,用户id为:{}",empId);
            BaseContext.setCurrentId(empId);
            long id = Thread.currentThread().getId();
            log.info("当前线程id为{}",id);
            filterChain.doFilter(request,response);
            return;
        }
        // 判断移动用户是否登录
        if(request.getSession().getAttribute("user") != null){
            Long userId = (Long)request.getSession().getAttribute("user");
            log.info("已登录,用户id为:{}",userId);
            BaseContext.setCurrentId(userId);
            long id = Thread.currentThread().getId();
            log.info("当前线程id为{}",id);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        // 如果未登录则返回未登录结果，通过输出流向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 检查本次请求是否需要放行
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url:urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
