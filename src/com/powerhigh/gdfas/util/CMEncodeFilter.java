package com.powerhigh.gdfas.util;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Description: 字符集Filter类 <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-3-5
 * @author mohui
 * @version 1.0
 * Modifier：
 * Modify Time：
 */

public class CMEncodeFilter implements Filter
 {
    private FilterConfig config = null;

    // default to ASCII
    private String targetEncoding = "gb2312";

    /**
     * 功能说明
     */
    public void destroy()
     {
        config = null;
        targetEncoding = null;
     }

    /**
     * 功能说明
     *
     * @param srequest 说明
     * @param sresponse 说明
     * @param chain 说明
     *
     * @throws IOException 说明
     * @throws ServletException 说明
     */
    public void doFilter(ServletRequest srequest, ServletResponse sresponse,
        FilterChain chain) throws IOException, ServletException
     {
        HttpServletRequest request = (HttpServletRequest) srequest;
        request.setCharacterEncoding(targetEncoding);
        
        // move on to the next
        chain.doFilter(srequest, sresponse);
     }

    /**
     * 功能说明
     *
     * @param config 说明
     *
     * @throws ServletException 说明
     */
    public void init(FilterConfig config) throws ServletException
     {
        this.config = config;
        this.targetEncoding = config.getInitParameter("encoding");
     }
 }
