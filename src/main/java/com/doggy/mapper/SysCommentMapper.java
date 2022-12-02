package com.doggy.mapper;

import com.doggy.entity.Comment;
import com.doggy.entity.ImageRepo;
import com.doggy.utils.Page;

import java.util.HashMap;
import java.util.List;

public interface SysCommentMapper {
    List<Comment> queryAllComments(HashMap<String, Object> param);

    List<Comment> queryAllCommentsDFS(int fid, int good_id);

    void insertBatchComments_ImageRepo(List<ImageRepo> list);

    void insertComments(Comment comment);

    List<Comment> pageQueryCommentData(Page page);

    int pageQueryCommentCount(Page page);
}