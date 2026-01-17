package com.example.repository;

import com.example.common.dto.PaginatedResponse;
import com.example.common.entity.User;
import com.example.common.repository.IUserRepository;
import com.example.repository.internal.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class MongoUserRepositoryImpl implements IUserRepository {

    private final SpringDataUserRepository springDataUserRepository;
    private final MongoTemplate mongoTemplate;

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

    @Override
    public PaginatedResponse<User> searchUsers(String keyword, boolean includeInactive, boolean includeDeleted,
            int page, int size) {
        Query query = new Query();
        Criteria criteria = new Criteria();

        // Status filters
        if (!includeDeleted) {
            query.addCriteria(Criteria.where("deleted").is(false));
        }
        if (!includeInactive) {
            query.addCriteria(Criteria.where("active").is(true));
        }

        // Keyword search
        if (keyword != null && !keyword.isBlank()) {
            String regex = ".*" + Pattern.quote(keyword) + ".*";
            criteria.orOperator(
                    Criteria.where("firstName").regex(regex, "i"),
                    Criteria.where("lastName").regex(regex, "i"),
                    Criteria.where("displayName").regex(regex, "i"),
                    Criteria.where("email").regex(regex, "i"),
                    Criteria.where("phoneNumber").regex(regex, "i"));
            query.addCriteria(criteria);
        }

        long total = mongoTemplate.count(query, User.class);
        Pageable pageable = PageRequest.of(page, size);
        query.with(pageable);

        List<User> items = mongoTemplate.find(query, User.class);

        return PaginatedResponse.<User>builder()
                .items(items)
                .totalItems(total)
                .totalPages((int) Math.ceil((double) total / size))
                .currentPage(page)
                .pageSize(size)
                .build();
    }
}
