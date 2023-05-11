package com.example.drools.controller;

import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.drools.model.ClaseRegla;
//import pe.gob.contraloria.crss.models.Order;

@RestController
public class MegaIOfferController {

	@Autowired
	private KieSession session;
	
	@PostMapping("/order")
	public ClaseRegla orderNow(@RequestBody ClaseRegla order) {
		session.insert(order);
		session.fireAllRules();
		
		/*Runtime runtime = Runtime.getRuntime();
		long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Memoria utilizada: " + memoryUsed + " bytes");*/
		
		
		return order;
	}
}
