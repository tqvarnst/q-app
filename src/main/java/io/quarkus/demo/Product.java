package io.quarkus.demo;

public class Product {

  public String id;
  public String name;
  public String description;
  public double price;
  
  public Product() {}

  public Product(String id, String name, String description, double price) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
  }
}