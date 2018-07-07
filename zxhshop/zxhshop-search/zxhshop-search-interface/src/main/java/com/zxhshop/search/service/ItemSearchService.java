package com.zxhshop.search.service;

import java.util.Map;

public interface ItemSearchService {
    Map<String,Object> search(Map<String, Object> searchMap);

//    Map<String, Object> searchItemList(Map<String, Object> searchMap);
}
