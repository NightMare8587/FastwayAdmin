package com.consumers.fastwayadmin.Dish;

public class CreateDishClass {
    public String name,image,half,full,mrp,count,totalRate,rating,enable;

    public CreateDishClass(String name,String image,String half,String full,String mrp,String count,String totalRate,String rating,String enable){
        this.full = full;
        this.name = name;
        this.count = count;
        this.totalRate = totalRate;
        this.rating = rating;
        this.enable = enable;
        this.mrp = mrp;
        this.half = half;
        this.image = image;
    }
}
