package com.example.Image_Upload.controller;

import com.example.Image_Upload.model.ImageInfo;
import com.example.Image_Upload.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ImageController {
	private final FilesStorageService storageService;
	
	@Autowired
	public ImageController(FilesStorageService storageService) {
		this.storageService = storageService;
	}
	
	@GetMapping("/")
	public String homepage(){
		return "redirect:/images";
	}
	
	/* Display list images */
	
	@GetMapping("/images")
	public String getListImages(Model model){
		List<ImageInfo> imageInfos = storageService.loadAll().map(path -> {
			String fileName = path.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(ImageController.class, "getImage", path.getFileName().toString())
					.build().toString();
			return new ImageInfo(fileName, url);
		}).toList();
		
		model.addAttribute("imageList", imageInfos);
		return "images";
	}
	
	@GetMapping("/images/{filename:.+}")
	public ResponseEntity<Resource> getImage(@PathVariable String filename){
		Resource file = storageService.load(filename);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
				+ file.getFilename()+"\"")
				.body(file);
	}
	
	/* -------------------------------------------- */
	
	/* -------------- UPLOAD IMAGES ----------------*/
	@GetMapping("/images/new")
	public String newImage(Model model){
		return "upload_form";
	}
	
	@PostMapping("/images/upload")
	public String uploadImage(@RequestParam("file")MultipartFile file,
	                          RedirectAttributes redirectAttributes){
		String message = "";
		
		try {
			if (file.isEmpty()) {
				message = "File is empty";
				redirectAttributes.addFlashAttribute("message", message);
				return "redirect:/images/new";
			}
			
			storageService.save(file);
			
			message = "Upload the imgage successfully!: " + file.getOriginalFilename();
			redirectAttributes.addFlashAttribute("message", message);
		} catch (Exception e){
			message = "Could not upload the image!: " + file.getOriginalFilename() +".Error: " + e.getMessage();
			redirectAttributes.addFlashAttribute("message", message);
		}
		return "redirect:/images/new";
	}
	
	@GetMapping("images/delete/{filename:.+}")
	public String deleteImage(@PathVariable String filename, RedirectAttributes redirectAttributes){
		try {
			boolean deleted = storageService.delete(filename);
			
			if (deleted){
				redirectAttributes.addFlashAttribute("message", "Image deleted successfully!");
			} else {
				redirectAttributes.addFlashAttribute("message", "Image could not be deleted!");
			}
		} catch (Exception e){
			redirectAttributes.addFlashAttribute("message", "Could not delete the image!");
		}
		
		return "redirect:/images";
	}
}
