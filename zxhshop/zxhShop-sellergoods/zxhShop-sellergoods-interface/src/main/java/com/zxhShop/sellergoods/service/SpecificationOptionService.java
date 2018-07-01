package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbSpecificationOption;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface SpecificationOptionService extends BaseService<TbSpecificationOption> {

    PageResult search(Integer page, Integer rows, TbSpecificationOption specificationOption);
}