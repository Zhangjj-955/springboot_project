package com.example.waimai.filter;

import com.alibaba.fastjson.JSON;
import com.example.waimai.common.BaseContext;
import com.example.waimai.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURL = request.getRequestURI();
        /*
        没有登陆时/employee/index.html能进入(WebConfig配置)，但是进入后又有/employee/page的请求，再次进入filter，判断登录状态，又回到login.html
         */
//        log.info(requestURL);
        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/logout",
                "/user/sendMsg"
        };
        for (String url :
                urls) {
            if (PATH_MATCHER.match(url, requestURL)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
/**
 * 记住要return！！！！惨痛的教训
 */
        if (request.getSession().getAttribute("employee") != null) {
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }
        if (request.getSession().getAttribute("user") != null) {
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            filterChain.doFilter(request, response);
            return;
        }
        servletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }
}
