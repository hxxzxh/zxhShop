package com.zxhshop.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zxhshop.common.util.IdWorker;
import com.zxhshop.mapper.OrderItemMapper;
import com.zxhshop.mapper.OrderMapper;
import com.zxhshop.mapper.PayLogMapper;
import com.zxhshop.order.service.OrderService;
import com.zxhshop.pojo.TbOrder;
import com.zxhshop.pojo.TbOrderItem;
import com.zxhshop.pojo.TbPayLog;
import com.zxhshop.service.impl.BaseServiceImpl;
import com.zxhshop.vo.Cart;
import com.zxhshop.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Transactional
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    //redis中购物车数据的key
    private static final String REDIS_CART_LIST = "CART_LIST";

    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public String addOrder(TbOrder order) {
        //支付日志id，若非微信支付可以为空
        String outTradeNo = "";
        //1.获取登录用户的购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(order.getUserId());
        if (cartList!=null&&cartList.size()>0) {
            //2 、遍历购物车列表的每个购物车对应生成一个订单和多个其对应的订单明细
            double totalFee = 0.0;
            String orderIds = "";//本次交易订单id的集合
            for (Cart cart : cartList) {
                long orderId = idWorker.nextId();
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(orderId);
                tbOrder.setSellerId(cart.getSellerId());
                tbOrder.setSourceType(order.getSourceType());
                tbOrder.setPaymentType(order.getPaymentType());
                tbOrder.setStatus("1");
                tbOrder.setReceiver(order.getReceiver());
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());
                tbOrder.setReceiverMobile(order.getReceiverMobile());
                tbOrder.setCreateTime(new Date());
                tbOrder.setUpdateTime(tbOrder.getCreateTime());
                //本订单的总金额
                double payment = 0.0;
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    orderItem.setOrderId(orderId);
                    orderItem.setId(idWorker.nextId());
                    payment += orderItem.getTotalFee().doubleValue();
                    orderItemMapper.insertSelective(orderItem);
                }
                tbOrder.setPayment(new BigDecimal(payment));
                orderMapper.insertSelective(tbOrder);

                //记录订单id
                if (orderIds.length() > 0) {
                    orderIds += "," + orderId;
                }else {
                    orderIds = orderId + "";
                }
                //累计本次订单的总金额
                totalFee += payment;
            }
            //3.如果是微信支付的话则需要生成支付日志保存到数据库中和redis中设置5分钟过期
            if ("1".equals(order.getPaymentType())) {
                outTradeNo = idWorker.nextId() + "";
                TbPayLog payLog = new TbPayLog();
                payLog.setOutTradeNo(outTradeNo);
                payLog.setTradeState("0");//未支付
                payLog.setUserId(order.getUserId());
                payLog.setTotalFee((long)(totalFee*100));//总金额，取整,因为提交到微信的金额要精确到分，所有乘以100
                payLog.setCreateTime(new Date());
                payLog.setPayType(order.getPaymentType());
                payLog.setOrderList(orderIds);//本次订单id集合

                payLogMapper.insertSelective(payLog);
            }
            //删除用户对应的购物车列表
            redisTemplate.boundHashOps(REDIS_CART_LIST).delete(order.getUserId());
        }
        return outTradeNo;
    }
}
















