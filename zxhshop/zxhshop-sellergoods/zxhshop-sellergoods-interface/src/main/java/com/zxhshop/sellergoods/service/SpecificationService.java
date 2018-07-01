package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbSpecification;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;
import com.zxhshop.vo.Specification;

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