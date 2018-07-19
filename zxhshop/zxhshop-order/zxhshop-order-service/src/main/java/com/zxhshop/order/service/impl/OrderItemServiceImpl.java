package com.zxhshop.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zxhshop.mapper.OrderItemMapper;
import com.zxhshop.pojo.TbOrderItem;
import com.zxhshop.order.service.OrderItemService;
import com.zxhshop.service.impl.BaseServiceImpl;
import com.zxhshop.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service(interfaceClass = OrderItemService.class)
public class OrderItemServiceImpl extends BaseServiceImpl<TbOrderItem> implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbOrderItem orderItem) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(orderItem.get***())){
            criteria.andLike("***", "%" + orderItem.get***() + "%");
        }*/

        List<TbOrderItem> list = orderItemMapper.selectByExample(example);
        PageInfo<TbOrderItem> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
