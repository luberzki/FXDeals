package com.luberzki.fxdeals.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvUtils {
	
	public <T> List<T> loadObjectList (Class<T> type, File file) {
		try {
			CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
			CsvMapper mapper = new CsvMapper();
			MappingIterator<T> readValues = mapper.reader(type).with(csvSchema).readValue(file);
			return readValues.readAll();
		}
		catch (IOException e) {
			
			return Collections.emptyList();
		}
	}
	
	public File convert(MultipartFile file) throws Exception {
		try {
			File convertedFile = new File(file.getOriginalFilename());
			convertedFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convertedFile);
			fos.write(file.getBytes());
			fos.close();
			return convertedFile;
		}
		catch (IOException e) {
			throw new Exception(e);
		}
	}
}
