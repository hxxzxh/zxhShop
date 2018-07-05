package com.zxh.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis-cluster.xml")
public class RedisClusterTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        redisTemplate.boundValueOps("string_key").set("哈哈");
        Object obj = redisTemplate.boundValueOps("string_key").get();
        System.out.println(obj);
    }

    @Test
    public void testHash() {
        redisTemplate.boundHashOps("hash_key").put("f1", "v1");
        redisTemplate.boundHashOps("hash_key").put("f2", "v2");
        Object obj = redisTemplate.boundHashOps("hash_key").get("f1");
        List list = redisTemplate.boundHashOps("hash_key").values();
        System.out.println(obj);
        System.out.println(list);
    }

    @Test
    public void testList() {
        redisTemplate.boundListOps("list_key").leftPush(1);
        redisTemplate.boundListOps("list_key").rightPush(2);
        List list_key = redisTemplate.boundListOps("list_key").range(0, -1);
        System.out.println(list_key);
    }

    @Test
    public void testSet() {
        redisTemplate.boundSetOps("set_key").add(8, 9, "好", 2, 5);
        Set set = redisTemplate.boundSetOps("set_key").members();
        System.out.println(set);
    }

    @Test
    public void testSortedSet() {
        redisTemplate.boundZSetOps("zset_key").add("aa", 2);
        redisTemplate.boundZSetOps("zset_key").add("bb", 3);
        Set zset = redisTemplate.boundZSetOps("zset_key").range(0, -1);
        System.out.println(zset);

    }







}
