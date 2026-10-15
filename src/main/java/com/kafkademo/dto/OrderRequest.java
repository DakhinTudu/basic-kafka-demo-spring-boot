package com.kafkademo.dto;

public record OrderRequest(  String orderId,
                             String item,
                             int quantity){

}
