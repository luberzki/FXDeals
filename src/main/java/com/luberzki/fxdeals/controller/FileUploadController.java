package com.luberzki.fxdeals.controller;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.luberzki.fxdeals.domain.CsvContent;
import com.luberzki.fxdeals.exception.StorageFileNotFoundException;
import com.luberzki.fxdeals.service.api.CsvContentService;
import com.luberzki.fxdeals.storage.StorageService;
import com.luberzki.fxdeals.utils.CsvUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class FileUploadController {
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private CsvContentService csvContentService;
	
	@Autowired
	private CsvUtils csvUtils;
	
	@Autowired
	private Validator validator;
	
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
		
		long startTime = System.currentTimeMillis();
		long success = 0;
		long failed = 0;
		
		CsvUtils csvUtils = new CsvUtils();
		File file = csvUtils.convert(multipartFile);
		ObjectReader objectReader = csvUtils.loadObject(CsvContent.class, file);
		
		List<CsvContent> list = new ArrayList<CsvContent>();
		
		try (Reader reader = new FileReader(file)) {
			MappingIterator<CsvContent> mi = objectReader.readValues(reader);
			while (mi.hasNext()) {
				CsvContent content = mi.next();
				list.add(content);
			}
		}
		
		for (CsvContent content : list) {
			Set<ConstraintViolation<CsvContent>> violations = validator.validate(content);
			if(violations.size() < 1) {
				success++;
			}
			else {
				failed++;
			}
		}

		long endTime = System.currentTimeMillis();
		log.debug("Start Time = " + startTime);
		log.debug("End Time = " + endTime);
		log.debug("Total Time = " + (endTime - startTime));
		
		storageService.store(multipartFile);
		redirectAttributes.addFlashAttribute("message", "You successfully uploaded" + (multipartFile).getOriginalFilename() + "! total = " + list.size() + ", success = " + success + ", failed = " + failed);
		return "redirect:/";
	}
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exception) {
		return ResponseEntity.notFound().build();
	}
}
