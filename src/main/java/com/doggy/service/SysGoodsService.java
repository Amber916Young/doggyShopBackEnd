package com.doggy.service;

import com.doggy.entity.Category;
import com.doggy.entity.Goods;
import com.doggy.entity.ImageRepo;
import com.doggy.mapper.SysGoodsMapper;
import com.doggy.utils.Page;
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

    public List<Goods> pageQueryGoodData(Page page) {
        return goodsMapper.pageQueryGoodData(page);
    }

    public int pageQueryGoodCount(Page page) {
        return goodsMapper.pageQueryGoodCount(page);
    }

    public List<Category> pageQueryCategoryData(Page page) {
        return goodsMapper.pageQueryCategoryData(page);
    }

    public int pageQueryCategoryCount(Page page) {
        return goodsMapper.pageQueryCategoryCount(page);
    }

    public Category queryCategoryById(int id) {
        return goodsMapper.queryCategoryById(id);
    }

    public void updateCategory(HashMap<String, Object> param) {
         goodsMapper.updateCategory(param);
    }

    public void insertCategory(HashMap<String, Object> param) {
        goodsMapper.insertCategory(param);
    }

    public void deleteCategory(HashMap<String, Object> param) {
        goodsMapper.deleteCategory(param);
    }

    public void updateGoods(HashMap<String, Object> param) {
        goodsMapper.updateGoods(param);
    }

    public int insertGoods(Goods goods ) {
      return   goodsMapper.insertGoods(goods);
    }

    public void deleteGoods(HashMap<String, Object> param) {
        goodsMapper.deleteGoods(param);
    }

    public void insertImageRepo(ImageRepo imageRepo) {
        goodsMapper.insertImageRepo(imageRepo);
    }

    public void deleteImageRepo(int fid) {
        goodsMapper.deleteImageRepo(fid);
    }

    public List<HashMap> queryAllTest() {
        return         goodsMapper.queryAllTest();

    }
}
