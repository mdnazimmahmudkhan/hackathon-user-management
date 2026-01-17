package com.example.repository;

import com.example.common.entity.User;
import com.example.common.repository.IUserRepository;
import com.example.repository.internal.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MongoUserRepositoryImpl implements IUserRepository {

    private final SpringDataUserRepository springDataUserRepository;

    @Override
    public Optional<User> getById(String id) {
        return springDataUserRepository.findById(id);
    }

    @Override
    public Collection<User> listAll() {
        return springDataUserRepository.findAll();
    }

    @Override
    public User add(User entity) {
        return springDataUserRepository.save(entity);
    }

    @Override
    public User update(User entity) {
        return springDataUserRepository.save(entity);
    }

    @Override
    public void delete(String id) {
        springDataUserRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return springDataUserRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return springDataUserRepository.existsByPhoneNumber(phoneNumber);
    }
}
