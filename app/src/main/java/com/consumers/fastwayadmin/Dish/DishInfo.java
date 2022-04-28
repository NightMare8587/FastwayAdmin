package com.consumers.fastwayadmin.Dish;

public class DishInfo {
    public String name,half,full,image,mrp,count,totalRate,rating,enable,description,dishType;
    public DishInfo(String name,String half,String full,String image,String mrp,String count,String totalRate,String rating,String enable,String description,String dishType){
        this.full = full;
        this.mrp = mrp;
        this.dishType = dishType;
        this.description = description;
        this.name = name;
        this.enable = enable;
        this.image = image;
        this.half = half;
        this.rating = rating;
        this.totalRate = totalRate;
        this.count = count;
    }
}
