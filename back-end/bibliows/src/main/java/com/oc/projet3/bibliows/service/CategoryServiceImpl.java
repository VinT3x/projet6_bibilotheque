package com.oc.projet3.bibliows.service;

import com.oc.projet3.bibliows.dao.CategoryRepository;
import com.oc.projet3.bibliows.dao.CategorySpecification;
import com.oc.projet3.bibliows.entities.Category;
import com.oc.projet3.bibliows.exceptions.WSException;
import com.oc.projet3.bibliows.exceptions.WSNotFoundExceptionException;

import com.oc.projet3.gs_ws.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * {@link CategoryService}
 */
@Service
// il faut spécifier quel transaction manager utiliser :
// No qualifying bean of type
// 'org.springframework.transaction.PlatformTransactionManager'
// available: expected single matching bean but found 2:
// transactionManager,resourcelessTransactionManager
@Transactional("transactionManager")
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * créer une categorie
     *
     * @param request
     * @return CreateCategoryResponse
     * @throws WSException
     */
    @Override
    public CreateCategoryResponse createCategory(CreateCategoryRequest request) throws WSException {
        CreateCategoryResponse response = new CreateCategoryResponse();

        Optional<Category> categoryOptional = categoryRepository.findByLabel(request.getLabel());
        if ( categoryOptional.isPresent())
            throw new WSNotFoundExceptionException("Cette catégorie existe déjà !");

        Category category = new Category();
        BeanUtils.copyProperties(request,category);

        Category categorySaved = categoryRepository.save(category);
        BeanUtils.copyProperties(categorySaved,response);

        return response;
    }

    /**
     * mise à jour d'une categorie
     *
     * @param request
     * @return UpdateCategoryResponse
     * @throws WSException
     */
    @Override
    public UpdateCategoryResponse updateCategory(UpdateCategoryRequest request) throws WSException {
        UpdateCategoryResponse response = new UpdateCategoryResponse();

        Optional<Category> categoryToUpdateOptional = categoryRepository.findById(request.getId().longValue());

        if ( ! categoryToUpdateOptional.isPresent())
            throw new WSNotFoundExceptionException("Cette catégorie n'existe pas !");

        Category categoryToUpdate = categoryToUpdateOptional.get();

        if ( request.getLabel() != null && ! request.getLabel().equals(categoryToUpdate.getLabel())) {
            Optional<Category> categoryOptional = categoryRepository.findByLabelAndIdIsNot(
                    request.getLabel(),
                    request.getId().longValue()
            );

            if (categoryOptional.isPresent())
                throw new WSNotFoundExceptionException("Cette catégorie existe déjà !");

            categoryToUpdate.setLabel(request.getLabel());
            categoryRepository.save(categoryToUpdate);
        }
        BeanUtils.copyProperties(categoryToUpdate, response);

        return response;
    }

    /**
     * rechercher des categories
     *
     * @param request
     * @return FindCategoriesResponse
     */
    @Override
    public FindCategoriesResponse findCategories(FindCategoriesRequest request) {
        FindCategoriesResponse response = new FindCategoriesResponse();

        Category categorySearch = new Category();
        categorySearch.setId(request.getIdCategory());
        categorySearch.setLabel(request.getLabel());

        CategorySpecification categorySpecification = new CategorySpecification(categorySearch);

        List<Category> categories = categoryRepository.findAll(categorySpecification);

        for (Category category: categories) {
            CategoryWS categoryWS = new CategoryWS();
            categoryWS.setLabel(category.getLabel());
            categoryWS.setId(category.getId());
            response.getCategories().add(categoryWS);
        }

        return response;
    }
}
