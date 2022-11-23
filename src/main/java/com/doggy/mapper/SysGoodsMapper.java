package com.doggy.mapper;

import com.doggy.entity.Category;
import com.doggy.entity.Goods;
import com.doggy.entity.ImageRepo;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:SysGoodsMapper
 * @Auther: yyj
 * @Description:
 * @Date: 22/11/2022 13:37
 * @Version: v1.0
 */
public interface SysGoodsMapper {
    List<Category> queryAllCategory(HashMap<String, Object> param);

    List<Goods> queryAllGoods(HashMap<String, Object> param);

    List<ImageRepo> queryAllImageList(HashMap<String, Object> param);

    Goods queryAllGoodsById(int id);

    Category querycurrentCategory(HashMap<String, Object> param);
}
