package com.zxhShop.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zxhShop.mapper.SpecificationOptionMapper;
import com.zxhShop.pojo.TbSpecificationOption;
import com.zxhShop.service.impl.BaseServiceImpl;
import com.zxhShop.vo.PageResult;
import com.zxhShop.mapper.SpecificationMapper;
import com.zxhShop.pojo.TbSpecification;
import com.zxhShop.sellergoods.service.SpecificationService;
import com.zxhShop.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpecificationService.class)
public class SpecificationServiceImpl extends BaseServiceImpl<TbSpecification> implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSpecification specification) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(specification.getSpecName())){
            criteria.andLike("spec_name", "%" + specification.getSpecName() + "%");
        }

        List<TbSpecification> list = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void add(Specification specification) {
        specificationMapper.insertSelective(specification.getSpecification());

        if (specification.getSpecificationOptionList() != null && specification.getSpecificationOptionList().size() > 0) {
            for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {
                specificationOption.setSpecId(specification.getSpecification().getId());
                specificationOptionMapper.insertSelective(specificationOption);
            }
        }
    }

    @Override
    public Specification findOne(Long id) {
        Specification specification = new Specification();

        specification.setSpecification(specificationMapper.selectByPrimaryKey(id));

        TbSpecificationOption param = new TbSpecificationOption();
        param.setSpecId(id);
        List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.select(param);
        specification.setSpecificationOptionList(specificationOptionList);

        return specification;
    }

    @Override
    public void update(Specification specification) {

        specificationMapper.updateByPrimaryKeySelective(specification.getSpecification());

        TbSpecificationOption param = new TbSpecificationOption();
        param.setSpecId(specification.getSpecification().getId());
        specificationOptionMapper.delete(param);

        if (specification.getSpecificationOptionList()!=null&&specification.getSpecificationOptionList().size()>0) {
            for (TbSpecificationOption specificationOption : specification.getSpecificationOptionList()) {
                //为修改中新增的规格选项设置选项id
                specificationOption.setSpecId(specification.getSpecification().getId());
                specificationOptionMapper.insertSelective(specificationOption);
            }
        }
    }

    @Override
    public void deletespecificationByIds(Long[] ids) {
        this.deleteByIds(ids);

        Example example = new Example(TbSpecificationOption.class);
        example.createCriteria().andIn("specId", Arrays.asList(ids));
        specificationOptionMapper.deleteByExample(example);

    }

    @Override
    public List<Map<String, Object>> selectOptionList() {
        return specificationMapper.selectOptionList();
    }
}
