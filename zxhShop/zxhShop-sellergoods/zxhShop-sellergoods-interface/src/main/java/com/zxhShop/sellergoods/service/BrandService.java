package com.zxhShop.sellergoods.service;

import com.zxhShop.pojo.TbBrand;
import com.zxhShop.service.BaseService;

import java.util.List;

public interface BrandService extends BaseService<TbBrand>{
    public List<TbBrand> queryAll();

    List<TbBrand> testPage(Integer page, Integer rows);
}
