package com.thinkgem.jeesite.modules.ftc.rest.customer;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.rest.BaseRestController;
import com.thinkgem.jeesite.common.rest.RestResult;
import com.thinkgem.jeesite.modules.ftc.constant.WishlistTypeEnum;
import com.thinkgem.jeesite.modules.ftc.convert.customer.WishlistConverter;
import com.thinkgem.jeesite.modules.ftc.entity.customer.Customer;
import com.thinkgem.jeesite.modules.ftc.entity.customer.Wishlist;
import com.thinkgem.jeesite.modules.ftc.entity.product.Product;
import com.thinkgem.jeesite.modules.ftc.service.customer.CustomerService;
import com.thinkgem.jeesite.modules.ftc.service.customer.WishlistService;
import com.thinkgem.jeesite.modules.ftc.service.product.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingbing on 2017/7/6.
 */
@RestController
@RequestMapping(value = "/rest/ftc/wishlist/")
@Api(value = "收藏管理", description = "收藏")
public class WishlistRestController extends BaseRestController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private ProductService productService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private WishlistConverter wishlistConverter;
    /**
     * 我的收藏
     *
     * @param
     * @return
     */
    @ApiOperation(value = "我的收藏列表", notes = "我收藏的商品和店铺")
    @RequestMapping(value = {"list"}, method = {RequestMethod.POST})
    public RestResult list(@RequestParam("token") String token, @RequestParam("type") String type,
                           HttpServletRequest request, HttpServletResponse response) {
        Customer customer = findCustomerByToken(token);
        if (customer != null) {
            Wishlist wishlist = new Wishlist();
            wishlist.setType(type);
            wishlist.setCustomer(customer);
            Page<Wishlist> wishlistPage = wishlistService.findPage(
                    new Page<Wishlist>(request, response), wishlist
            );
            if(wishlistPage.getCount()==0){
                return new RestResult(CODE_SUCCESS, "没有收藏");
            }
            List<Wishlist> wishlists=new ArrayList<>();
            if(type.equals("1")){
                //商品
                for(Wishlist wish:wishlistPage.getList()){

                    Product product=productService.getWithSpec(wish.getProduct().getId());
                    wish.setProduct(product);
                    wishlists.add(wish);
                }



            }else{
                //店铺
                for(Wishlist wish:wishlistPage.getList()){
                    Customer result = customerService.get(wish.getCustomer());
                    wish.setCustomer(result);
                    wishlists.add(wish);
                }

            }


            return new RestResult(CODE_SUCCESS, MSG_SUCCESS, wishlistConverter.convertListFromModelToDto(wishlists));
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
    public RestResult add(@RequestParam("token") String token, @RequestParam("id") String id,
                          @RequestParam("type") String type) {
        Customer customer = findCustomerByToken(token);
        if (customer != null) {
            Wishlist wishlist = new Wishlist();
            wishlist.setType(type);
            if (WishlistTypeEnum.WISHLIST_PRODUCT.getValue().equals(type)) {
                wishlist.setProduct(new Product(id));
            } else {
                wishlist.setDesigner(new Customer(id));
            }
            wishlist.setCustomer(customer);
            List<Wishlist> result = wishlistService.findList(wishlist);
            if(CollectionUtils.isNotEmpty(result)){
                if (WishlistTypeEnum.WISHLIST_PRODUCT.getValue().equals(type)) {
                    return new RestResult(CODE_ERROR, "您已经收藏过了");
                } else {
                    return new RestResult(CODE_ERROR, "您已经关注过了");
                }

            }else {
                wishlistService.save(wishlist);
            }
            return new RestResult(CODE_SUCCESS, MSG_SUCCESS, null);
        } else {
            return new RestResult(CODE_ERROR, "没有找到用户信息");
        }

    }

    /**
     * 取消收藏
     *
     * @param
     * @return
     */
    @ApiOperation(value = "取消收藏", notes = "取消收藏")
    @RequestMapping(value = {"delete"}, method = {RequestMethod.POST})
    public RestResult delete(@RequestParam("token") String token, @RequestParam("id") String id,@RequestParam("type") String type) {
        Customer customer = findCustomerByToken(token);
        if (customer != null) {
            Wishlist wishlist = new Wishlist();
            if (WishlistTypeEnum.WISHLIST_PRODUCT.getValue().equals(type)) {
                wishlist.setProduct(new Product(id));
            } else {
                wishlist.setDesigner(new Customer(id));
            }
            wishlist.setCustomer(customer);
            List<Wishlist> result = wishlistService.findList(wishlist);
            if(result!=null){
                wishlistService.delete(wishlist);
            }

            return new RestResult(CODE_SUCCESS, MSG_SUCCESS);
        } else {
            return new RestResult(CODE_ERROR, "没有找到用户信息");
        }

    }
}
