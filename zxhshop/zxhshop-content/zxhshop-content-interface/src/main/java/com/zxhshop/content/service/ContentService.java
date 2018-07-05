package com.zxhshop.content.service;

import com.zxhshop.pojo.TbContent;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

import java.util.List;

public interface ContentService extends BaseService<TbContent> {

    PageResult search(Integer page, Integer rows, TbContent content);

    List<TbContent> findContentListByCategoryId(Long categoryId);
}