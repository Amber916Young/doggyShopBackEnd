package com.doggy.mapper;

import com.doggy.entity.Comment;
import com.doggy.entity.ImageRepo;
import com.doggy.entity.Suggestion;
import com.doggy.utils.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

public interface SysCommentMapper {
    List<Comment> queryAllComments(HashMap<String, Object> param);

    List<Comment> queryAllCommentsDFS(int fid, int good_id);

    void insertBatchComments_ImageRepo(List<ImageRepo> list);

    void insertComments(Comment comment);

    List<Comment> pageQueryCommentData(Page page);

    int pageQueryCommentCount(Page page);

    @Delete("delete from DoggyPets.comment where comment_id = #{id} ")
    void deleteComment(HashMap<String, Object> param);

    void updateComment(HashMap<String, Object> param);


    List<Comment> pageQueryCommentDataByid(Page page);

    @Select("select from DoggyPets.comment where comment_id = #{id} ")
    Comment queryCommentsbyId(int id);

    void insertSuggestion(HashMap<String, Object> param);


    void updateSuggestion(HashMap<String, Object> param);


    List<Suggestion> querySuggestionPage(Page page);
}
