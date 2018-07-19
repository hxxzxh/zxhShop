package com.zxhshop.cart.service;

import com.zxhshop.vo.Cart;

import java.util.List;

public interface CartService {
    /**
     * 根据商品id查询商品和购买数量加入到cartList
     * @param cartList 购物车列表
     * @param itemId 商品id
     * @param num 购买数量
     * @return 购物车列表
     */
    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 根据用户id查询在redis对应的购物车列表
     * @param username 用户id
     * @return
     */
    List<Cart> findCartListByUsername(String username);

    /**
     * 将用户对应的购物车列表保存到redis中
     * @param newCartList 购物车列表
     * @param username 用户id
     */
    void saveCartListByUsername(List<Cart> newCartList, String username);

    /**
     * 合并cookie和redis中记录的购物车列表
     * @param cookie_cartList cookie中的购物车列表
     * @param redis_cartList redis中的购物车列表
     * @return 合并后的购物车列表
     */
    List<Cart> mergeCartList(List<Cart> cookie_cartList, List<Cart> redis_cartList);
}
