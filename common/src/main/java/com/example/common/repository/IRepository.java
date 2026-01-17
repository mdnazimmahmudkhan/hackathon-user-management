package com.example.common.repository;

import java.util.Collection;
import java.util.Optional;

public interface IRepository<TEntity, TId> {
    Optional<TEntity> getById(TId id);
    Collection<TEntity> listAll();
    TEntity add(TEntity entity);
    TEntity update(TEntity entity);
    void delete(TId id);
}
