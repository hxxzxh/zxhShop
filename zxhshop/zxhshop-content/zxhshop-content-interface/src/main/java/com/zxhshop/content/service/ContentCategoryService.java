package com.zxhshop.content.service;

import com.zxhshop.pojo.TbContentCategory;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface ContentCategoryService extends BaseService<TbContentCategory> {

    PageResult search(Integer page, Integer rows, TbContentCategory contentCategory);
}