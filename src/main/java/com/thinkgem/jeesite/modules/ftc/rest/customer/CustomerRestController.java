package com.thinkgem.jeesite.modules.ftc.rest.customer;

import com.thinkgem.jeesite.common.rest.BaseRestController;
import com.thinkgem.jeesite.common.rest.RestResult;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.modules.ftc.constant.FlagEnum;
import com.thinkgem.jeesite.modules.ftc.constant.PlatformTypeEnum;
import com.thinkgem.jeesite.modules.ftc.convert.customer.AddressConverter;
import com.thinkgem.jeesite.modules.ftc.convert.customer.CustomerConverter;
import com.thinkgem.jeesite.modules.ftc.dto.customer.AddressDto;
import com.thinkgem.jeesite.modules.ftc.entity.customer.Address;
import com.thinkgem.jeesite.modules.ftc.entity.customer.Customer;
import com.thinkgem.jeesite.modules.ftc.service.customer.AddressService;
import com.thinkgem.jeesite.modules.ftc.service.customer.CustomerService;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by houyi on 2017/6/17 0017.
 */
@RestController
@RequestMapping(value = "/rest/ftc/customer/")
@Api(value = "用户登录", description = "用户登录")
public class CustomerRestController extends BaseRestController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerConverter customerConverter;

    @Autowired
    private AddressConverter addressConverter;

    /**
     * 发送短信验证码
     *
     * @param mobile
     * @return
     */
    @ApiOperation(value = "发送短信验证码", notes = "用户登录首页和找回密码页面，发送短信验证码")
    @RequestMapping(value = {"sendShortMessage"}, method = {RequestMethod.POST})
    public RestResult sendShortMessage(@RequestParam("mobile") String mobile) {
        String captcha = getShortMessageNumber();
        EhCacheUtils.put(CAPTCHA_CACHE, mobile, captcha);
        return new RestResult(CODE_SUCCESS, MSG_SUCCESS, captcha);
    }

    /**
     * 注册用户
     *
     * @param mobile
     * @param password
     * @param captcha
     * @return
     */
    @Deprecated
    @ApiOperation(value = "用户注册", notes = "用户注册，密码为密文")
    @RequestMapping(value = {"register"}, method = {RequestMethod.POST})
    public RestResult register(@RequestParam("mobile") String mobile, @RequestParam("password") String password, @RequestParam("captcha") String captcha) {
        String random = (String) EhCacheUtils.get(CAPTCHA_CACHE, mobile);
        if (captcha.equals(random)) {
            Customer param = new Customer();
            param.setTelephone(mobile);
            List<Customer> result = customerService.findList(param);
            if (CollectionUtils.isEmpty(result)) {
                Customer customer = new Customer();
                customer.setTelephone(mobile);
                customer.setLoginPassword(password);
                customerService.save(customer);
                return new RestResult(CODE_SUCCESS, MSG_SUCCESS);
            } else {
                return new RestResult(CODE_ERROR, "该手机号已被注册了");
            }
        } else {
            return new RestResult(CODE_ERROR, "手机验证码不正确！");
        }
    }

    /**
     * 重置密码
     *
     * @param mobile
     * @param password
     * @param captcha
     * @return
     */
    @Deprecated
    @ApiOperation(value = "重置密码", notes = "重置密码，密码为密文")
    @RequestMapping(value = {"resetPassword"}, method = {RequestMethod.POST})
    public RestResult resetPassword(@RequestParam("mobile") String mobile, @RequestParam("password") String password, @RequestParam("captcha") String captcha) {
        String random = (String) EhCacheUtils.get(CAPTCHA_CACHE, mobile);
        if (captcha.equals(random)) {
            Customer param = new Customer();
            param.setTelephone(mobile);
            List<Customer> result = customerService.findList(param);
            if (CollectionUtils.isNotEmpty(result)) {
                Customer customer = result.get(0);
                customer.setLoginPassword(password);
                customerService.save(customer);
                return new RestResult(CODE_SUCCESS, MSG_SUCCESS);
            } else {
                return new RestResult(CODE_ERROR, "该手机号没有注册用户");
            }
        } else {
            return new RestResult(CODE_ERROR, "手机验证码不正确！");
        }
    }

    /**
     * 用户登录
     *
     * @param platformType
     * @param userId
     * @param password
     * @return
     */
    @ApiOperation(value = "用户登录", notes = "支持短信、微信和QQ登录")
    @RequestMapping(value = {"login"}, method = {RequestMethod.POST})
    public RestResult login(@RequestParam("type") String platformType, @RequestParam("pUid") String userId,
                            @RequestParam("password") String password) {
        if (PlatformTypeEnum.Phone.getValue().equals(platformType)) {
            return loginByShotMessage(userId, password);
        } else if (PlatformTypeEnum.WeChat.getValue().equals(platformType)) {
            return null;
        } else if (PlatformTypeEnum.QQ.getValue().equals(platformType)) {
            return null;
        } else {
            return new RestResult(CODE_ERROR, "不支持的登录方式");
        }
    }

    /**
     * 短信登录
     *
     * @param mobile
     * @param captcha
     * @return
     */
    private RestResult loginByShotMessage(String mobile, String captcha) {
        Customer param = new Customer();
        param.setTelephone(mobile);
        List<Customer> result = customerService.findList(param);
        if (CollectionUtils.isNotEmpty(result)) {
            Customer customer = result.get(0);
            String captchaCache = (String) EhCacheUtils.get(CAPTCHA_CACHE, mobile);
            if (captchaCache.equals(captcha)) {
                String token = UUID.randomUUID().toString();
                customer.setAccessToken(token);
                customer.setExpiresTime(new Date());
                EhCacheUtils.put(TOKEN_CACHE, token, customer);
                return new RestResult(CODE_SUCCESS, MSG_SUCCESS, customer);
            } else {
                return new RestResult(CODE_ERROR, "短信验证码不正确");
            }
        } else {
            return new RestResult(CODE_ERROR, "没有找到该用户信息");
        }

    }

    @ApiOperation(value = "使用令牌登录", notes = "使用令牌登录")
    @RequestMapping(value = {"loginByToken"}, method = {RequestMethod.POST})
    public RestResult loginByToken(@RequestParam("token") String token) {
        Customer customer = findCustomerByToken(token);
        if (customer != null) {
            return new RestResult(CODE_SUCCESS, MSG_SUCCESS, customer);
        } else {
            return new RestResult(CODE_ERROR, "没有找到用户信息");
        }
    }

    /**
     * 更新用户信息
     *
     * @param token
     * @param customer
     * @return
     */
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息")
    @RequestMapping(value = {"updateCustomer"}, method = {RequestMethod.POST})
    public RestResult updateCustomer(@RequestParam("token") String token, Customer customer) {
        Customer loginCustomer = findCustomerByToken(token);
        if (loginCustomer == null) {
            return new RestResult(CODE_NULL, "令牌无效，请重新登录！");
        } else {
            customerService.save(customer);
            EhCacheUtils.put(TOKEN_CACHE, token, customer);
            return new RestResult(CODE_SUCCESS, MSG_SUCCESS);
        }
    }

    @ApiOperation(value = "获取地区列表", notes = "用户编辑收货地址时的备选条件")
    @RequestMapping(value = {"findAreaList"}, method = {RequestMethod.GET})
    public RestResult findAreaList() {
        List<Area> areaList = areaService.findAll();
        return new RestResult(CODE_SUCCESS, MSG_SUCCESS, areaList);
    }

    @ApiOperation(value = "获取收货地址列表", notes = "用户选择收货地址时显示")
    @RequestMapping(value = {"findAddressList"}, method = {RequestMethod.GET})
    public RestResult findAddressList(@RequestParam("token") String token) {
        Customer customer = findCustomerByToken(token);
        if (customer == null) {
            return new RestResult(CODE_NULL, "令牌无效，请重新登录！");
        } else {
            Address param = new Address();
            param.setCustomer(customer);
            List<Address> addressList = addressService.findList(param);
            List<AddressDto> addressDtoList = addressConverter.convertListFromModelToDto(addressList);
            return new RestResult(CODE_SUCCESS, MSG_SUCCESS, addressDtoList);
        }
    }

    @ApiOperation(value = "获取默认收货地址", notes = "用户下单时默认的收货地址")
    @RequestMapping(value = {"findDefualtAddress"}, method = {RequestMethod.GET})
    public RestResult findDefualtAddress(@RequestParam("token") String token) {
        Customer customer = findCustomerByToken(token);
        if (customer == null) {
            return new RestResult(CODE_NULL, "令牌无效，请重新登录！");
        } else {
            Address param = new Address();
            param.setCustomer(customer);
            param.setIsDefault(FlagEnum.Flag_YES.getValue());
            List<Address> addressList = addressService.findList(param);
            if (CollectionUtils.isNotEmpty(addressList)) {
                Address address = addressList.get(0);
                return new RestResult(CODE_SUCCESS, MSG_SUCCESS, addressConverter.convertModelToDto(address));
            } else {
                return new RestResult(CODE_ERROR, "当前用户没有设置默认收货地址");
            }
        }
    }

    @ApiOperation(value = "保存收货地址", notes = "用户新建或修改收货地址时使用，修改时需要地址标识。")
    @RequestMapping(value = {"saveAddress"}, method = {RequestMethod.POST})
    public RestResult saveAddress(@RequestParam("token") String token, AddressDto address) {
        Customer customer = findCustomerByToken(token);
        if (customer == null) {
            return new RestResult(CODE_NULL, "令牌无效，请重新登录！");
        } else {
            addressService.save(addressConverter.convertDtoToModel(address));
            return new RestResult(CODE_SUCCESS, MSG_SUCCESS);
        }
    }

}

