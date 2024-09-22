package com.example.Image_Upload;

import com.example.Image_Upload.service.FilesStorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageUploadApplication implements CommandLineRunner {
	@Resource
	FilesStorageService filesStorageService;
	
	public static void main(String[] args) {
		SpringApplication.run(ImageUploadApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		filesStorageService.init();
	}
}
