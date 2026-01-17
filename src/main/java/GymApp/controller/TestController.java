package GymApp.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RequestMapping("/api/test")
@RestController
public class TestController {

    @GetMapping()
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<String> test() {
        return List.of("element_1", "element_2", "element_3");
    }

}
