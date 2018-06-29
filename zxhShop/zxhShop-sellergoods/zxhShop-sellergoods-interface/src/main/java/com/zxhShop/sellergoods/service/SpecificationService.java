package com.zxhShop.sellergoods.service;

import com.zxhShop.pojo.TbSpecification;
import com.zxhShop.service.BaseService;
import com.zxhShop.vo.PageResult;
import com.zxhShop.vo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService extends BaseService<TbSpecification> {

    PageResult search(Integer page, Integer rows, TbSpecification specification);

    void add(Specification specification);

    Specification findOne(Long id);


    void update(Specification specification);

    void deletespecificationByIds(Long[] ids);

    List<Map<String,Object>> selectOptionList();
}