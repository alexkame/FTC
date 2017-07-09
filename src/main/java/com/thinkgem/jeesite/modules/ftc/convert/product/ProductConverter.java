package com.thinkgem.jeesite.modules.ftc.convert.product;

import com.thinkgem.jeesite.common.rest.BaseConverter;
import com.thinkgem.jeesite.modules.ftc.convert.customer.CustomerConverter;
import com.thinkgem.jeesite.modules.ftc.dto.customer.CustomerDto;
import com.thinkgem.jeesite.modules.ftc.dto.product.ProductImageDto;
import com.thinkgem.jeesite.modules.ftc.dto.product.ProductDto;
import com.thinkgem.jeesite.modules.ftc.dto.product.ProductSpecDto;
import com.thinkgem.jeesite.modules.ftc.entity.customer.Customer;
import com.thinkgem.jeesite.modules.ftc.entity.product.Category;
import com.thinkgem.jeesite.modules.ftc.entity.product.Product;
import com.thinkgem.jeesite.modules.ftc.entity.product.ProductSpec;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bingbing on 2017/6/17.
 */
@Component
public class ProductConverter extends BaseConverter<Product, ProductDto> {
    @Autowired
    private ProductSpecConverter productSpecConverter;
    @Autowired
    private ProductImageConverter productImageConverter;
    @Autowired
    private CustomerConverter customerConverter;
    @Override
    public Product convertDtoToModel(ProductDto dto) {
        Product model = new Product();
        model.setId(dto.getId());
        model.setCategory(new Category(dto.getCategoryId()));

        return model;
    }

    @Override
    public ProductDto convertModelToDto(Product model) {
        ProductDto dto = new ProductDto();
        if(model==null)return dto;
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDesc(model.getIntroduce());
        dto.setOpen(false);
        dto.setPicImg(model.getPicImg());
        dto.setPrice(model.getShowPrice());
        dto.setDesignPrice(model.getDesignPrice());
        dto.setCategoryId(model.getCategory().getId());
        Customer customer=model.getDesignBy();
        CustomerDto customerDto=customerConverter.convertModelToDto(customer);

        List<ProductImageDto> textures= productImageConverter.convertListFromModelToDto(model.getImages());
        dto.setTextures(textures);
        List<ProductSpecDto> attrs=
                productSpecConverter.convertListFromModelToDto(model.getSpecs());
        dto.setAttrs(attrs);

        return dto;
    }


}
