package com.doggy.service;

import com.doggy.entity.Category;
import com.doggy.entity.Goods;
import com.doggy.entity.ImageRepo;
import com.doggy.mapper.SysGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:SysGoodsService
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 13:37
 * @Version: v1.0
 */
@Service
public class SysGoodsService {
    @Autowired
    SysGoodsMapper goodsMapper;

    public List<Category> queryAllCategory(HashMap<String, Object> param) {
       return goodsMapper.queryAllCategory(param);
    }

    public List<Goods> queryAllGoods(HashMap<String, Object> param) {
        return goodsMapper.queryAllGoods(param);
    }

    public List<ImageRepo> queryAllImageList(HashMap<String, Object> param) {
        return goodsMapper.queryAllImageList(param);
    }


    public Goods queryAllGoodsById(int id) {
        return goodsMapper.queryAllGoodsById(id);
    }

    public Category querycurrentCategory(HashMap<String, Object> param) {
        return goodsMapper.querycurrentCategory(param);
    }
}
