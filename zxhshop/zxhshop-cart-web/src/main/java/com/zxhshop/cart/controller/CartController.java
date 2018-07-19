package com.zxhshop.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONArray;
import com.zxhshop.cart.service.CartService;
import com.zxhshop.common.util.CookieUtils;
import com.zxhshop.vo.Cart;
import com.zxhshop.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {

    //Cookie中购物车列表的名称
    private static final String COOKIE_CART_LIST = "PYG_CART_LIST";
    private static final int COOKIE_CART_LIST_MAX_AGE = 3600*24;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;

    /**
     * 获取购物车列表数据；如果登录了则从redis中获取，若未登录则从cookie中获取
     *
     * @return 购物车列表
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //获取cookie中的购物车列表
        String cartListJsonStr = CookieUtils.getCookieValue(request, COOKIE_CART_LIST, true);
        List<Cart> cookie_cartList;
        if (!StringUtils.isEmpty(cartListJsonStr)) {
            cookie_cartList = JSONArray.parseArray(cartListJsonStr, Cart.class);
        } else {
            cookie_cartList = new ArrayList<>();
        }
        if ("anonymousUser".equals(username)) {
                return cookie_cartList;
        } else {//已登录，从redis中获取购物车列表数据
            List<Cart> redis_cartList = cartService.findCartListByUsername(username);
            if (cookie_cartList.size() > 0) {
                //合并两个购物车列表
                redis_cartList = cartService.mergeCartList(cookie_cartList, redis_cartList);
                //保存最新的购物车列表到redis中
                cartService.saveCartListByUsername(redis_cartList,username);
                //删除cookie中的购物车列表
                CookieUtils.deleteCookie(request, response, COOKIE_CART_LIST);
            }
            return redis_cartList;
        }
    }

    @GetMapping("/getUsername")
    public Map<String, Object> getUsername() {
        Map<String, Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果未登录，那么获取到的username为：anonymousUser
        map.put("username", username);
        return map;
    }

    /**
     * 实现登录，未登录下将商品加入购物车列表
     * CrossOrigin 表示支持跨域请求资源
     * origins 表示允许的域名；allowCredentials表示允许携带并接收cookie（可以省略）
     * @param itemId 商品id
     * @param num 购买数量
     * @return
     */
    @GetMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://item.zxhshop.com",allowCredentials = "true")
    public Result addItemToCartList(Long itemId, Integer num) {
        try {
           /* //设置允许跨域请求
            response.setHeader("Access-Control-Allow-Origin", "http://item.zxhshop.com");
            //允许携带并接收cookie
            response.setHeader("Access-Control-Allow-Credentials","true");*/

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取购物车列表
            List<Cart> cartList = findCartList();
            //将商品加入到购物车列表
            List<Cart> newCartList = cartService.addItemToCartList(cartList,itemId, num);

            if ("anonymousUser".equals(username)) {
                //未登录，加入cookie
                String cartListJsonStr = JSONArray.toJSONString(newCartList);
                CookieUtils.setCookie(request, response,COOKIE_CART_LIST,cartListJsonStr,COOKIE_CART_LIST_MAX_AGE, true);
            }else {
                //已登录，写入到redis
                cartService.saveCartListByUsername(newCartList, username);
            }
            return Result.ok("加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.ok("写入购物车失败");
        }


    }
}
