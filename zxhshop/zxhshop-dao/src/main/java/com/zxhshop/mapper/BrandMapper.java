package com.zxhshop.mapper;

import com.zxhshop.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<TbBrand>{
    public List<TbBrand> queryAll();


    List<Map<String,Object>> selectOptionList();
}
