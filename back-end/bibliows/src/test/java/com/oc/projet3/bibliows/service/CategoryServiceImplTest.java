package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.CategoryRepository;
import com.oc.projet3.bibliows.entities.Category;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.exceptions.WSNotFoundExceptionException;
import com.oc.projet3.gs_ws.CreateCategoryRequest;
import com.oc.projet3.gs_ws.CreateCategoryResponse;
import com.oc.projet3.gs_ws.UpdateCategoryRequest;
import com.oc.projet3.gs_ws.UpdateCategoryResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @MockBean// permet de définir des comportements sur une partie des méthodes
    private CategoryRepository categoryRepository;

    @Test
    void createCategoryAlreadyExist() {
        //GIVEN
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setLabel("Test");

        Category category = new Category();
        category.setId(1L);
        category.setLabel("Test");

        //WHEN
        Mockito.when(categoryRepository.findByLabel(any())).thenReturn(Optional.of(category));

        //THEN
        assertThrows(WSNotFoundExceptionException.class, () -> categoryService.createCategory(request));

    }

    @Test
    void createCategory() throws WSException {
        //GIVEN
        CreateCategoryResponse response = new CreateCategoryResponse();
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setLabel("Test");

        Category category = new Category();
        category.setId(1L);
        category.setLabel("Test");

        //WHEN
        Mockito.when(categoryRepository.findByLabel(any())).thenReturn(Optional.empty());
        Mockito.when(categoryRepository.save(any())).thenReturn(category);

        //THEN
        response = categoryService.createCategory(request);
        assertEquals(response.getCategory().getLabel(), request.getLabel());

    }


    @Test
    void updateCategoryWithCategoryNoExist() {
        //GIVEN
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setLabel("Test");
        request.setId(BigInteger.valueOf(1));

        Category category = new Category();
        category.setId(1L);
        category.setLabel("Test");

        //WHEN
        Mockito.when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        //THEN
        assertThrows(WSNotFoundExceptionException.class, () -> categoryService.updateCategory(request));
    }

    @Test
    void updateCategoryWithAnotherCategoryAlreadyExist() {
        //GIVEN
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setLabel("Test modifié");
        request.setId(BigInteger.valueOf(1));

        Category category = new Category();
        category.setId(1L);
        category.setLabel("Test");

        Category catExist = new Category();
        catExist.setId(1L);
        catExist.setLabel("Test");

        //WHEN
        Mockito.when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.findByLabelAndIdIsNot(request.getLabel(), request.getId().intValue())).thenReturn(Optional.of(catExist));

        //THEN
        assertThrows(WSNotFoundExceptionException.class, () -> categoryService.updateCategory(request));
    }

    @Test
    void updateCategory() throws WSException {
        //GIVEN
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setLabel("Test modifié");
        request.setId(BigInteger.valueOf(1));

        Category category = new Category();
        category.setId(1L);
        category.setLabel("Test");

        Category catExist = new Category();
        catExist.setId(1L);
        catExist.setLabel("Test");

        //WHEN
        Mockito.when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.findByLabelAndIdIsNot(request.getLabel(), request.getId().intValue())).thenReturn(Optional.empty());
        Mockito.when(categoryRepository.save(any())).thenReturn(null);

        //THEN
        UpdateCategoryResponse response = categoryService.updateCategory(request);
        assertEquals(response.getCategory().getLabel(), request.getLabel());

    }
}