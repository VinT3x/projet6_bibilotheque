package com.oc.projet3.bibliows.dao;

import com.oc.projet3.bibliows.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByLabel(String label);

    Optional<Category> findByLabelAndIdIsNot(String label, long id);

    List<Category> findAllByIdOrLabelOrderByLabel(long id, String label);
}
