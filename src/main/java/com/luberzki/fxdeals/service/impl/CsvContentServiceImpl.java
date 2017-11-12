package com.luberzki.fxdeals.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.luberzki.fxdeals.domain.CsvContent;
import com.luberzki.fxdeals.repo.CsvContentRepository;
import com.luberzki.fxdeals.service.api.CsvContentService;

@Service
public class CsvContentServiceImpl implements CsvContentService {

	@Autowired
	private CsvContentRepository csvContentRepository;

	@Override
	public CsvContent save(CsvContent content) {
		return csvContentRepository.save(content);
	}
	
}
