package com.flying.test.model;

import com.flying.framework.annotation.Param;
import com.flying.framework.metadata.Meta;

@Meta(id="Order",title="订单", table="txs_order")
public class Order {
	@Param(value="order_no")
	private String orderNo;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
}
