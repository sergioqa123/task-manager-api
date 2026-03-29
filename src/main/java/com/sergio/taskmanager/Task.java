package com.sergio.taskmanager;

import org.springframework.data.annotation.Id;

record Task(@Id Long id, String title, String description, boolean completed, String owner) {
    
}
