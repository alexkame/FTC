package com.thinkgem.jeesite.modules.ftc.convert.order;

import com.thinkgem.jeesite.common.rest.BaseConverter;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ftc.convert.customer.AddressConverter;
import com.thinkgem.jeesite.modules.ftc.dto.order.OrderDto;
import com.thinkgem.jeesite.modules.ftc.dto.order.ShoppingCartDto;
import com.thinkgem.jeesite.modules.ftc.entity.order.Order;
import com.thinkgem.jeesite.modules.ftc.entity.order.OrderProduct;
import com.thinkgem.jeesite.modules.ftc.entity.order.ShoppingCart;
import com.thinkgem.jeesite.modules.ftc.service.order.OrderService;
import com.thinkgem.jeesite.modules.ftc.service.product.ProductSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houyi on 2017/7/8 0008.
 */
@Component
public class OrderConverter extends BaseConverter<Order, OrderDto> {

    @Autowired
    private AddressConverter addressConverter;

    @Autowired
    private ShoppingCartConverter shoppingCartConverter;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductSpecService productSpecService;

    @Override
    public Order convertDtoToModel(OrderDto dto) {
        Order order = new Order();
        order.setId(dto.getId());
        order.setOrderNo(dto.getOrder());
        order.setOrderStatus(dto.getType());
        order.setInvoiceTitle(dto.getReceipt());
        order.setRemarks(dto.getAddition());
        order.setAddress(addressConverter.convertDtoToModel(dto.getAddress()));

        List<OrderProduct> orderProductList = new ArrayList<OrderProduct>();
        for(ShoppingCartDto shoppingCartDto : dto.getBags()){
            ShoppingCart shoppingCart = shoppingCartConverter.convertDtoToModel(shoppingCartDto);
            OrderProduct orderProduct = orderService.cart2OrderProduct(order, shoppingCart);
            orderProductList.add(orderProduct);
        }
        order.setOrderProductList(orderProductList);
        return order;
    }

    @Override
    public OrderDto convertModelToDto(Order model) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(model.getId());
        orderDto.setOrder(model.getOrderNo());
        orderDto.setType(model.getOrderStatus());
        orderDto.setReceipt(model.getInvoiceTitle());
        orderDto.setAddition(model.getRemarks());
        orderDto.setAddress(addressConverter.convertModelToDto(model.getAddress()));

        List<ShoppingCartDto> shoppingCartDtoList = new ArrayList<ShoppingCartDto>();
        for(OrderProduct orderProduct : model.getOrderProductList()){
            ShoppingCart shoppingCart = orderService.orderProduct2Cart(orderProduct);

            String productSpecId = shoppingCart.getProductSpec().getId();
            if (StringUtils.isNotEmpty(productSpecId)) {
                shoppingCart.setProductSpec(productSpecService.getWithImages(productSpecId));
            }

            ShoppingCartDto shoppingCartDto = shoppingCartConverter.convertModelToDto(shoppingCart);
            shoppingCartDtoList.add(shoppingCartDto);
        }
        orderDto.setBags(shoppingCartDtoList);
        return orderDto;
    }
}
