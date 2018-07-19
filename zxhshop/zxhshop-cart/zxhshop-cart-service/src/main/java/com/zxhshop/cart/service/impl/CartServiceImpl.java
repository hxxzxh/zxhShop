package com.zxhshop.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zxhshop.cart.service.CartService;
import com.zxhshop.common.util.CookieUtils;
import com.zxhshop.mapper.ItemMapper;
import com.zxhshop.pojo.TbItem;
import com.zxhshop.pojo.TbOrderItem;
import com.zxhshop.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {

    //redis中购物车数据的key
    private static final String REDIS_CART_LIST = "CART_LIST";

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.验证商品是否存在，商品的启用状态是否启用
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("商品状态不合法");
        }

        String sellerId = item.getSellerId();
        Cart cart = findCartBySellerId(cartList, sellerId);


        if (cart == null) {

            if (num > 0) {
                //2.如果该商品对应的商家不存在购物车列表中，则重新添加商家及其对应的商品
                cart = new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(item.getSeller());

                List<TbOrderItem> orderItemList = new ArrayList<>();
                TbOrderItem orderItem = creatOrderItem(item, num);
                orderItemList.add(orderItem);
                cart.setOrderItemList(orderItemList);

                cartList.add(cart);
            } else {
                throw new RuntimeException("购买数量不合法");
            }
        } else {
            //3.如果该商品对应的商家存在在购物车列表中；那么判断该商品是否存在，若存在则计算数量，否则将新商品加入到该商家
            TbOrderItem orderItem = findOrderItemByItemId(cart.getOrderItemList(), itemId);
            if (orderItem != null) {
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                //若购买数小于等于0，则需要将该商品删除
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }

                //若该购物车的商品对应的商家商品列表等于0，则需要删除该商家的列表
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }

            } else {
                if (num > 0) {
                    orderItem = creatOrderItem(item, num);
                    cart.getOrderItemList().add(orderItem);
                } else {
                    throw new RuntimeException("购买数量不合法");
                }
            }
        }

        return cartList;
    }

    @Override
    public List<Cart> findCartListByUsername(String username) {
        List<Cart> cartList = (List<Cart>)redisTemplate.boundHashOps(REDIS_CART_LIST).get(username);
        if (cartList != null) {
            return cartList;
        }
        return new ArrayList<>();
    }

    @Override
    public void saveCartListByUsername(List<Cart> newCartList, String username) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(username, newCartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookie_cartList, List<Cart> redis_cartList) {
        //任何一个集合合并都可以；商品不存在则新增，存在则购买数量叠加
        for (Cart cart : cookie_cartList) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                addItemToCartList(redis_cartList, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redis_cartList;
    }

    /**
     * 根据商家id在购物车列表中查询商家对应的购物车
     *
     * @param cartList 购物车列表
     * @param sellerId 商家id
     * @return 购物车
     */
    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null && cartList.size() > 0) {
            for (Cart cart : cartList) {
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }
            }
        }
        return null;
    }

    /**
     * 在购物车商品明细列表里根据商品id查找对应的商品明细
     *
     * @param orderItemList 购物车商品明细列表
     * @param itemId        商品id
     * @return 购物车明细
     */
    private TbOrderItem findOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        if (orderItemList != null && orderItemList.size() > 0) {
            for (TbOrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().equals(itemId)) {
                    return orderItem;
                }
            }
        }
        return null;
    }

    /**
     * 构造购物车商品明细
     *
     * @param item 添加商品
     * @param num  购买数量
     * @return 商品明细
     */
    private TbOrderItem creatOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setNum(num);
        orderItem.setItemId(item.getId());
        orderItem.setTitle(item.getTitle());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

}
