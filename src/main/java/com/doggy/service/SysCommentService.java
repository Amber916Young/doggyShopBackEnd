package com.doggy.service;

import com.doggy.entity.Comment;
import com.doggy.entity.ImageRepo;
import com.doggy.mapper.SysCommentMapper;
import com.doggy.mapper.SysGoodsMapper;
import com.doggy.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName:SysCommentService
 * @Auther: yyj
 * @Description:
 * @Date: 30/11/2022 12:37
 * @Version: v1.0
 */
@Service
public class SysCommentService {


    @Autowired
    SysCommentMapper commentMapper;

    public List<Comment> queryAllComments(HashMap<String, Object> param) {
        return commentMapper.queryAllComments(param);
    }


    public List<Comment> queryAllCommentsDFS(int fid, int good_id) {
        return commentMapper.queryAllCommentsDFS(fid,good_id);
    }

    public void insertBatchComments_ImageRepo(List<ImageRepo> list) {
         commentMapper.insertBatchComments_ImageRepo(list);
    }

    public void insertComments(Comment comment) {
        commentMapper.insertComments(comment);
    }

    public List<Comment> pageQueryCommentData(Page page) {
        return commentMapper.pageQueryCommentData(page);
    }

    public int pageQueryCommentCount(Page page) {
        return commentMapper.pageQueryCommentCount(page);
    }

    public void deleteComment(HashMap<String, Object> param) {
         commentMapper.deleteComment(param);
    }
}
