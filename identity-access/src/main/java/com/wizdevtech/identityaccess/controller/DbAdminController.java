package com.wizdevtech.identityaccess.controller;

import com.wizdevtech.identityaccess.model.User;
import com.wizdevtech.identityaccess.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // Ensure this import exists
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/admin/db")
@RequiredArgsConstructor
public class DbAdminController {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Set<String> AVAILABLE_ROLES = Set.of("ROLE_USER", "ROLE_ADMIN");

    private final UserService userService;

    @GetMapping("/users")
    public String showDbUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        Pageable pageable = PageRequest.of(
                page - 1,
                DEFAULT_PAGE_SIZE,
                Sort.by(Sort.Direction.fromString(direction), sort)
        );

        Page<User> userPage = userService.findFilteredUsers(search, status, pageable);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("searchTerm", search);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("availableRoles", AVAILABLE_ROLES);

        return "db-admin";
    }

    @PostMapping("/users/{id}/enable")
    public String enableUser(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        userService.enableUser(id);
        redirectAttrs.addFlashAttribute("successMessage", "User enabled successfully");
        return "redirect:/admin/db/users";
    }

    @PostMapping("/users/{id}/disable")
    public String disableUser(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        userService.disableUser(id);
        redirectAttrs.addFlashAttribute("successMessage", "User disabled successfully");
        return "redirect:/admin/db/users";
    }
}