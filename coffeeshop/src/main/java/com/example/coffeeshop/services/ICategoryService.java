package com.example.coffeeshop.services;

import com.example.coffeeshop.dtos.CategoryDTO;
import com.example.coffeeshop.entitys.Category;

public interface ICategoryService {
    Category createCategory(CategoryDTO categoryDTO);
    Category getCategorById(long id);
    Category getAllCategories();
    Category updateCategory(long categoryId, CategoryDTO categoryDTO);
    void deleteCategory(long id);
}
