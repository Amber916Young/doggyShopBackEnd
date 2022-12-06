package com.doggy.mapper;

import com.doggy.entity.Category;
import com.doggy.entity.Goods;
import com.doggy.entity.ImageRepo;
import com.doggy.utils.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

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

    List<Goods> pageQueryGoodData(Page page);

    int pageQueryGoodCount(Page page);

    int pageQueryCategoryCount(Page page);

    List<Category> pageQueryCategoryData(Page page);


    @Select("select * from DoggyPets.Category where id = #{id}")
    Category queryCategoryById(int id);

    void updateCategory(HashMap<String, Object> param);

    void insertCategory(HashMap<String, Object> param);

    @Delete("delete from DoggyPets.Category where id = #{id}")
    void deleteCategory(HashMap<String, Object> param);

    void updateGoods(HashMap<String, Object> param);

    int insertGoods(Goods goods );

    @Delete("delete from DoggyPets.goods where id = #{id}")
    void deleteGoods(HashMap<String, Object> param);

    void insertImageRepo(ImageRepo imageRepo);

    @Delete("delete from DoggyPets.ImageRepo where fid = #{fid}")
    void deleteImageRepo(int fid);

    List<HashMap> queryAllTest();
}
