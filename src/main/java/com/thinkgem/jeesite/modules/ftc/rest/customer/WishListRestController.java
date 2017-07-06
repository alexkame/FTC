package com.thinkgem.jeesite.modules.ftc.rest.customer;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.rest.BaseRestController;
import com.thinkgem.jeesite.common.rest.RestResult;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.modules.ftc.constant.WishlistTypeEnum;
import com.thinkgem.jeesite.modules.ftc.entity.customer.Customer;
import com.thinkgem.jeesite.modules.ftc.entity.customer.Wishlist;
import com.thinkgem.jeesite.modules.ftc.entity.product.Product;
import com.thinkgem.jeesite.modules.ftc.service.customer.WishlistService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by bingbing on 2017/7/6.
 */
@RestController
@RequestMapping(value = "/rest/ftc/wishlist/")
@Api(value = "收藏管理", description = "收藏")
public class WishListRestController extends BaseRestController {
    @Autowired
    private WishlistService wishlistService;

    /**
     * 我的收藏
     *
     * @param
     * @return
     */
    @ApiOperation(value = "我的收藏列表", notes = "我收藏的商品和店铺")
    @RequestMapping(value = {"list"}, method = {RequestMethod.POST})
    public RestResult list(@RequestParam("token") String token, @RequestParam("type") String type, HttpServletRequest request, HttpServletResponse response) {
        Customer customer = findCustomerByToken(token);
        if (customer != null) {
            Wishlist wishlist = new Wishlist();
            wishlist.setType(type);
            wishlist.setCustomer(customer);
            Page<Wishlist> wishlistPage = wishlistService.findPage(
                    new Page<Wishlist>(request, response), wishlist
            );

            return new RestResult(CODE_SUCCESS, MSG_SUCCESS, wishlistPage.getList());
        } else {
            return new RestResult(CODE_ERROR, "没有找到用户信息");
        }

    }

    /**
     * 加入收藏
     *
     * @param
     * @return
     */
    @ApiOperation(value = "加入收藏", notes = "我收藏的商品和店铺")
    @RequestMapping(value = {"add"}, method = {RequestMethod.POST})
    public RestResult add(@RequestParam("token") String token, @RequestParam("id") String id, @RequestParam("type") String type, HttpServletRequest request, HttpServletResponse response) {
        Customer customer = findCustomerByToken(token);
        if (customer != null) {
            Wishlist wishlist = new Wishlist();
            wishlist.setType(type);
            if (WishlistTypeEnum.WISHLIST_PRODUCT.getValue().equals(type)) {
                wishlist.setProduct(new Product(id));
            } else {
                wishlist.setCustomer(new Customer(id));
            }
            wishlist.setCustomer(customer);
            wishlist.preInsert();
            wishlistService.save(wishlist);
            return new RestResult(CODE_SUCCESS, MSG_SUCCESS, null);
        } else {
            return new RestResult(CODE_ERROR, "没有找到用户信息");
        }

    }
    /**
     * 加入收藏
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除收藏", notes = "删除收藏")
    @RequestMapping(value = {"delete"}, method = {RequestMethod.POST})
    public RestResult delete(@RequestParam("token") String token, @RequestParam("id") String id,HttpServletRequest request, HttpServletResponse response) {
        Customer customer = findCustomerByToken(token);
        if (customer != null) {
            wishlistService.delete(new Wishlist(id));
            return new RestResult(CODE_SUCCESS, MSG_SUCCESS, null);
        } else {
            return new RestResult(CODE_ERROR, "没有找到用户信息");
        }

    }
}
