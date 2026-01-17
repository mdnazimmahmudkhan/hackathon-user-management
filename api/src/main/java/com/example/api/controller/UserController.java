package com.example.api.controller;

import com.example.common.dto.UserCreateRequest;
import com.example.common.dto.UserResponse;
import com.example.common.dto.UserUpdateRequest;
import com.example.common.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable String id, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<com.example.common.dto.PaginatedResponse<UserResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.searchUsers(keyword, includeInactive, includeDeleted, page, size));
    }

    @GetMapping
    public ResponseEntity<Collection<UserResponse>> listAll() {
        return ResponseEntity.ok(userService.listAllUsers());
    }
}
