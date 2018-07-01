package com.zxhshop.sellergoods.service.impl;

import com.zxhshop.pojo.TbBrand;
import org.springframework.beans.factory.annotation.Autowired;

/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")*/
public class BrandServiceImplTest {
  @Autowired
//    private BrandService brandService;

//  @Test
    public void tet1(){
      TbBrand tbBrand = new TbBrand();
      tbBrand.setName("长虹");
   // tbBrand.setFirstChar("T");
//    PageResult p = this.brandService.search(tbBrand,1,10);
//    System.out.println(p.getRows());
  }

}
