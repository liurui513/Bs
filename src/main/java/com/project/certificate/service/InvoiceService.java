package com.project.certificate.service;

import com.project.certificate.custom.CommonService;
import com.project.certificate.entity.Invoice;
import com.project.certificate.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService extends CommonService<Invoice, Integer> {
	@Autowired
	private InvoiceRepository invoiceDAO;

	public Invoice findByNumber(String number) {
		return invoiceDAO.findByNumber(number);
	}

}
