package com.zxhShop.sellergoods.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.zxhShop.mapper.BrandMapper;
import com.zxhShop.pojo.TbBrand;
import com.zxhShop.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service(interfaceClass = BrandService.class)
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<TbBrand> queryAll() {

        return brandMapper.queryAll();
    }
}
