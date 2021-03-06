package com.thinkgem.jeesite.modules.ftc.convert.product;

import com.thinkgem.jeesite.common.rest.BaseConverter;
import com.thinkgem.jeesite.modules.ftc.dto.product.DesignDetailDto;
import com.thinkgem.jeesite.modules.ftc.dto.product.ProductImageDto;
import com.thinkgem.jeesite.modules.ftc.entity.product.DesignDetail;
import com.thinkgem.jeesite.modules.ftc.entity.product.Position;
import org.springframework.stereotype.Component;

/**
 * Created by bingbing on 2017/7/4.
 */
@Component
public class DesignDetailConverter extends BaseConverter<DesignDetail,DesignDetailDto>{
    @Override
    public DesignDetailDto convertModelToDto(DesignDetail model) {
        DesignDetailDto dto=new DesignDetailDto();
        if(model==null)return dto;
        dto.setRotation(model.getRotation());
        dto.setScale(model.getScale());
        dto.setId(model.getInfo());
        ProductImageDto texture=new ProductImageDto();
        texture.setImgUrl(model.getPicImg());
        texture.setImgNailUrl(model.getPicImg());
        texture.setId(model.getInfo());
        dto.setTexture(texture);
        dto.setX(model.getX());
        dto.setY(model.getY());
        return dto;
    }

    @Override
    public DesignDetail convertDtoToModel(DesignDetailDto dto) {
        DesignDetail model=new DesignDetail();
        model.setPicImg(dto.getTexture().getImgUrl());
        model.setScale(dto.getScale());
        model.setRotation(dto.getRotation());
        model.setX(dto.getX());
        model.setY(dto.getY());
        model.setInfo(dto.getId());
        return model;
    }
}
