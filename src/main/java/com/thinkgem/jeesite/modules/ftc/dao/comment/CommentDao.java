/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ftc.dao.comment;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.ftc.entity.comment.Comment;

/**
 * 评论DAO接口
 * @author wangqingxiang
 * @version 2017-06-04
 */
@MyBatisDao
public interface CommentDao extends CrudDao<Comment> {
	public void updateProductCommentCount(String id);
}