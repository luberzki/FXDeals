package com.luberzki.fxdeals.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CsvUtils {
	
	public <T> ObjectReader loadObject (Class<T> type, File file) {
//		try {
			CsvSchema csvSchema = CsvSchema.builder().addColumn("dealId").addColumn("fromISOCurrency").addColumn("toISOCurrency")
					.addColumn("dealTimestamp").addColumn("dealAmount").build().withSkipFirstDataRow(true);
			CsvMapper mapper = new CsvMapper();
			ObjectReader objectReader = mapper.readerFor(type).with(csvSchema);

//			try (Reader reader = new FileReader(file)) {
//				MappingIterator<T> mi = objectReader.readValues(reader);
//				while (mi.hasNext()) {
//					mi.next();
//				}
//			}
			
			return objectReader;
//		}
//		catch (IOException e) {
//			log.debug("CsvUtils IOException : " + e);
//			
//			return null;
//		}
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
