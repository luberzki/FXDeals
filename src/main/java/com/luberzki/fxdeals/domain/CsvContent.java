package com.luberzki.fxdeals.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class CsvContent {

	@Id
	private long id;
	
	@NotNull
	private String dealId;
	
	@NotNull
	@Size(min = 3, max = 3)
	private String fromISOCurrency;
	
	@NotNull
	@Size(min = 3, max = 3)
	private String toISOCurrency;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dealTimestamp;
	
	@NotNull
	@Min(0)
	private Double dealAmount;
}
