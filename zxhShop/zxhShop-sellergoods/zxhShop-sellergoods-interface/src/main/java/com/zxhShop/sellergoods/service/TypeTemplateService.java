package com.zxhShop.sellergoods.service;

import com.zxhShop.pojo.TbTypeTemplate;
import com.zxhShop.service.BaseService;
import com.zxhShop.vo.PageResult;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate);
}