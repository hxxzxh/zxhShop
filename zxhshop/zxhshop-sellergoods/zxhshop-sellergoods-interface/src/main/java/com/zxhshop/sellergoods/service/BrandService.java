package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbBrand;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService extends BaseService<TbBrand>{
    public List<TbBrand> queryAll();

    List<TbBrand> testPage(Integer page, Integer rows);

    PageResult search(TbBrand brand, Integer page, Integer rows);

    List<Map<String,Object>> selectOptionList();

}
