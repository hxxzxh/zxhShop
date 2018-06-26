package com.zxhShop.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zxhShop.mapper.BrandMapper;
import com.zxhShop.pojo.TbBrand;
import com.zxhShop.sellergoods.service.BrandService;
import com.zxhShop.service.impl.BaseServiceImpl;
import com.zxhShop.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

//service的注解来自dubbo
@Service(interfaceClass = BrandService.class)
public class BrandServiceImpl extends BaseServiceImpl<TbBrand> implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<TbBrand> queryAll() {

        return brandMapper.queryAll();
    }

    @Override
    public List<TbBrand> testPage(Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
//        return brandMapper.queryAll();
        return brandMapper.selectAll();
    }

    @Override
    public PageResult search(TbBrand brand, Integer page, Integer rows) {
        //设置分页
        PageHelper.startPage(page, rows);
        //设置查询条件
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(brand.getFirstChar())) {
            criteria.andEqualTo("firstChar", brand.getFirstChar());
        }
        if (!StringUtils.isEmpty(brand.getName())) {
            criteria.andLike("name", "%"+brand.getName()+"%");
        }
        List<TbBrand> list = brandMapper.selectByExample(example);
        PageInfo<TbBrand> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
}
