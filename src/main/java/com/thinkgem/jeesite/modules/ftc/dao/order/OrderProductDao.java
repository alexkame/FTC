/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ftc.dao.order;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.ftc.entity.order.OrderProduct;

/**
 * 订单DAO接口
 * @author houyi
 * @version 2017-05-28
 */
@MyBatisDao
public interface OrderProductDao extends CrudDao<OrderProduct> {

    /**
     * 销售统计
     * @param orderProduct
     * @return
     */
    OrderProduct findSoldInfo(OrderProduct orderProduct);
}