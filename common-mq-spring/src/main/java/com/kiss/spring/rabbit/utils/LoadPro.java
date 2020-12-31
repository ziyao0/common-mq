package com.kiss.spring.rabbit.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Map;

/**
 * 读取配置文件工具类
 *
 * @author zhangziyao
 * @date 2020/12/26 9:15 下午
 */
public class LoadPro {
    /**
     * 配置文件转换map集合
     *
     * @param path 配置文件路径
     * @return 返回map集合
     * @throws IOException 异常信息
     */
    public static Map<Object, Object> getPro(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return PropertiesLoaderUtils.loadProperties(resource);
    }
}
