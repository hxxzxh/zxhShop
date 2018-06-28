package com.zxhShop.sellergoods.service;

import com.zxhShop.pojo.TbSpecificationOption;
import com.zxhShop.service.BaseService;
import com.zxhShop.vo.PageResult;

public interface SpecificationOptionService extends BaseService<TbSpecificationOption> {

    PageResult search(Integer page, Integer rows, TbSpecificationOption specificationOption);
}