package com.pjcdarker.util;

import org.junit.Test;

import java.util.Map;

/**
 * @author pjc
 * @create 2016-10-09
 */
public class TestPropertiesUtil {

    private static final String PROPERTIES_PATH = "/app.properties";

    @Test
    public void testGet() {
        String username = PropertiesUtil.get(PROPERTIES_PATH, "username");
        System.out.println("username: " + username);
    }

    @Test
    public void testToMap() {
        Map<String, String> map = PropertiesUtil.toMap(PROPERTIES_PATH);
        System.out.println("map: " + map);
    }

    @Test
    public void testToJsonString() {
        String jsonString = PropertiesUtil.toJsonString(PROPERTIES_PATH);
        System.out.println("json: " + jsonString);
    }
}
