package com.example.waimai.common;

public class BaseContext {
    /**
     * 有的地方想获取id但是没有session，可以通过ThreadLocal获取
     */
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static Long getCurrentId(){
        return threadLocal.get();
    }
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
}
