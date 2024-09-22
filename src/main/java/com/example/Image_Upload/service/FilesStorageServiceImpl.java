package com.example.Image_Upload.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {
	private final Path root = Paths.get("./uploads");
	
	@Override
	public void init() {
		try {
			Files.createDirectories(root);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload: " + e.getMessage(), e);
		}
	}
	
	@Override
	public Resource load(String filename) {
		try {
			Path file = root.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read file: " + filename);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage(), e);
		}
	}
	
	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(root, 1)
					.filter(path -> !path.equals(root))
					.map(path -> root.relativize(path));
		} catch (IOException e){
			throw new RuntimeException("Could not load files", e);
		}
	}
	
	@Override
	public void save(MultipartFile file) {
//		try {
//			Path destination = root.resolve(file.getOriginalFilename());
//			Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
//		} catch (Exception e) {
//			if (e instanceof FileAlreadyExistsException) {
//				throw new RuntimeException("A file of that name already exists");
//			}
//
//			throw new RuntimeException(e.getMessage());
//		}
		
		try {
			Path destination = root.resolve(file.getOriginalFilename());
			Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
		} catch (FileAlreadyExistsException e){
			throw new RuntimeException("File already exists: " + file.getOriginalFilename(), e);
		} catch (IOException e) {
			throw new RuntimeException("An I/O error occurred: " + e.getMessage(), e);
		} catch (Exception e) {
			throw new RuntimeException("An unknown error occurred: " + e.getMessage(), e);
		}
	}
	
	@Override
	public boolean delete(String filename) {
		try {
			Path file = root.resolve(filename);
			return Files.deleteIfExists(file);
		} catch (IOException e){
			throw new RuntimeException("Could not delete file: " + filename, e);
		}
	}
	
	@Override
	public boolean deleteAll() {
		return FileSystemUtils.deleteRecursively(root.toFile());
	}
}
