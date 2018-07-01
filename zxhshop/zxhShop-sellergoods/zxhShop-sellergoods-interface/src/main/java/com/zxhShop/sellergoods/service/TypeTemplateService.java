package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbTypeTemplate;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate);
}