package com.tomiru.crudapp.services;

import com.tomiru.crudapp.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductsRepository extends JpaRepository<Product, Integer> {

}
