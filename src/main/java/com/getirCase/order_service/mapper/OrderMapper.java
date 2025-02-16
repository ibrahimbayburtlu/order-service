package com.getirCase.order_service.mapper;

import com.getirCase.order_service.model.response.OrderResponse;
import com.getirCase.order_service.model.request.OrderRequest;
import com.getirCase.order_service.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderResponse OrderToDTO(Order order);
    Order OrderDTOToEntity(OrderResponse orderResponse);
    Order OrderRequestToEntity(OrderRequest orderRequest);
    OrderResponse OrderRequestToDto(Order order);
}
