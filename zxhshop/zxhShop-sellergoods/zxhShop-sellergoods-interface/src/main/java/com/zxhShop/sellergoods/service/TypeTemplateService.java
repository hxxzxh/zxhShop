package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbTypeTemplate;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate);

    List<Map> findSpecList(Long id);

    /**
     * 更新分类模板（品牌，规格）缓存数据
     * 分类模板id作为field，品牌列表为value
     * 分类模板id作为field，规格列表为value
     */
    void updateTypeTemplateToRedis();
}