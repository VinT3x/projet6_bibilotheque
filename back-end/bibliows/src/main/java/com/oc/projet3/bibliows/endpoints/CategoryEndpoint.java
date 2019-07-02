package com.oc.projet3.bibliows.endpoints;

import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.exceptions.WSNotFoundExceptionException;
import com.oc.projet3.bibliows.service.CategoryService;
import com.oc.projet3.gs_ws.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CategoryEndpoint {

    private final CategoryService categoryService;
    private static final String NAMESPACE_URI = "http://oc.com/projet3/bibliows/service/biblio-producing-web-service";

    @Autowired
    public CategoryEndpoint(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createCategoryRequest")
    @ResponsePayload
    public CreateCategoryResponse createCategory(@RequestPayload CreateCategoryRequest request) throws WSException {

        return categoryService.createCategory(request);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateCategoryRequest")
    @ResponsePayload
    public UpdateCategoryResponse updateCategory(@RequestPayload UpdateCategoryRequest request)
            throws WSException {

        return categoryService.updateCategory(request);

    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "findCategoriesRequest")
    @ResponsePayload
    public FindCategoriesResponse FindCategories(@RequestPayload FindCategoriesRequest request) throws WSNotFoundExceptionException {

        return categoryService.findCategories(request);

    }
}
