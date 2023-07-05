package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {
     Map<String, Order> orderMap = new HashMap<>();
     Map<String, DeliveryPartner> deliveryPartnerMap = new HashMap<>();
     Map<String, String> orderToPartnerDb = new HashMap<>();
     Map<String, List<String>> partnerToOrderDb = new HashMap<>();


    public void addOrder(Order order) {
        //add order to the order db
        orderMap.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        //add count to the partner db
        deliveryPartnerMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if (orderMap.containsKey(orderId) && deliveryPartnerMap.containsKey(partnerId)) {
            orderToPartnerDb.put(orderId, partnerId);
             List<String> orderList = new ArrayList<>();
             //add orders to the partner to order db
            if (partnerToOrderDb.containsKey(partnerId)) {
                orderList = partnerToOrderDb.get(partnerId);
            }
            orderList.add(orderId);
            partnerToOrderDb.put(partnerId, orderList);

            //increase the size of partner
            DeliveryPartner deliveryPartner = deliveryPartnerMap.get(partnerId);
            deliveryPartner.setNumberOfOrders(orderList.size());
        }
    }

    public Order getOrderById(String orderId) {
        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return deliveryPartnerMap.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        return partnerToOrderDb.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        return partnerToOrderDb.get(partnerId);
    }

    public List<String> getAllOrders() {
        List<String> orders = new ArrayList<>();
        for (String key : orderMap.keySet()) {
            orders.add(key);
        }
        return orders;
    }

    public Integer getCountOfUnassignedOrders() {
        return orderMap.size() - orderToPartnerDb.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(int newtime, String partnerId) {
        int cnt = 0;
        List<String> orders = partnerToOrderDb.get(partnerId);
        for (int i = 0; i < orders.size(); i++) {
            int deliveryTime = orderMap.get(orders.get(i)).getDeliveryTime();
            if (deliveryTime > newtime)cnt++;
        }
        return cnt;
    }

    public int getLastDeliveryTimeByPartnerId(String partnerId) {
        int maxTime = 0;
        List<String> orders = partnerToOrderDb.get(partnerId);
        for(String key : orders) {
            int time = orderMap.get(key).getDeliveryTime();
            maxTime = Math.max(time, maxTime);
        }
        return maxTime;
    }

    public void deletePartnerById(String partnerId) {
        deliveryPartnerMap.remove(partnerId);
        List<String> orders = partnerToOrderDb.get(partnerId);
        partnerToOrderDb.remove(partnerId);

        for(String order : orders) {
            orderToPartnerDb.remove(order);
        }
    }

    public void deleteOrderById(String orderId) {
        orderMap.remove(orderId);

        String partnerId = orderToPartnerDb.get(orderId);
        orderToPartnerDb.remove(orderId);

        partnerToOrderDb.get(partnerId).remove(orderId);
        deliveryPartnerMap.get(partnerId).setNumberOfOrders(partnerToOrderDb.get(partnerId).size());
    }
}
