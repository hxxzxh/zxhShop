package com.zxhshop.mapper;

import com.zxhshop.pojo.TbSpecification;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecificationMapper extends Mapper<TbSpecification> {

    List<Map<String,Object>> selectOptionList();
}
