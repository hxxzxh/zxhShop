package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbTypeTemplate;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate);

    List<Map> findSpecList(Long id);
}