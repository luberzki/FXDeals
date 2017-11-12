package com.luberzki.fxdeals.controller;

import java.io.File;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.luberzki.fxdeals.domain.CsvContent;
import com.luberzki.fxdeals.exception.StorageFileNotFoundException;
import com.luberzki.fxdeals.service.api.CsvContentService;
import com.luberzki.fxdeals.storage.StorageService;
import com.luberzki.fxdeals.utils.CsvUtils;

@Controller
public class FileUploadController {
	
	private final StorageService storageService;
	
	private CsvContentService csvContentService;
	
	@Autowired
	public FileUploadController(StorageService storageService, CsvContentService csvContentService) {
		this.storageService = storageService;
		this.csvContentService = csvContentService;
	}
	
	@GetMapping("/")
	public String listUploadedFiles(Model model) {
		
		model.addAttribute("files", storageService.loadAll().map(path -> MvcUriComponentsBuilder.fromMethodName(
				FileUploadController.class, "serveFile", path.getFileName().toString()).build().toString()).collect(Collectors.toList()));
		return "uploadForm";
	}
	
	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" 
				+ file.getFilename() + "\"").body(file);
	}
	
	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws Exception {
		
		CsvUtils csvUtils = new CsvUtils();
		File file = csvUtils.convert(multipartFile);
		csvUtils.loadObjectList(CsvContent.class, file);
		
		storageService.store((MultipartFile) file);
		redirectAttributes.addFlashAttribute("message", "You successfully uploaded" + ((MultipartFile) file).getOriginalFilename() + "!");
		return "redirect:/";
	}
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exception) {
		return ResponseEntity.notFound().build();
	}
}
