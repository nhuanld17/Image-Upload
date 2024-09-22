package com.example.Image_Upload.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {
	
	public void init();
	
	public void save(MultipartFile file);
	
	public Resource load(String filename);
	
	public Stream<Path> loadAll();
	
	public boolean delete(String filename);
	
	public boolean deleteAll();
}
