package com.thinkgem.jeesite.modules.ftc.convert.product;

import com.thinkgem.jeesite.common.rest.BaseConverter;
import com.thinkgem.jeesite.modules.ftc.dto.product.ProductSpecDto;
import com.thinkgem.jeesite.modules.ftc.entity.product.ProductSpec;
import org.springframework.stereotype.Component;

/**
 * Created by wangqingxiang on 2017/6/20.
 */
@Component
public class ProductSpecConverter extends BaseConverter<ProductSpec,ProductSpecDto> {
    @Override
    public ProductSpecDto convertModelToDto(ProductSpec model) {
        ProductSpecDto dto=new ProductSpecDto();
        dto.setId(model.getId());

        return dto;
    }

    @Override
    public ProductSpec convertDtoToModel(ProductSpecDto dto) {
        ProductSpec model=new ProductSpec();
        model.setId(dto.getId());
        return model;
    }
}
