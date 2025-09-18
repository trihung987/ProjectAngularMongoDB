package me.trihung.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import me.trihung.service.DataService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data")
public class DataController {

    @Autowired
    private DataService dataService;

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(dataService.getEventCategories());
    }

    @GetMapping("/provinces")
    public ResponseEntity<List<String>> getProvinces() {
        return ResponseEntity.ok(dataService.getProvinces());
    }

    @GetMapping("/banks")
    public ResponseEntity<List<String>> getBanks() {
        return ResponseEntity.ok(dataService.getBanks());
    }
}