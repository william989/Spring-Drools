package com.example.drools.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.shared.utils.io.IOUtil;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import org.apache.maven.shared.utils.io.IOUtil;

@Configuration
public class DroolConfig {
	private KieServices kieServices = KieServices.Factory.get();
	
	@Value("${repo.drools}") 
	String url;
	
	@Value("${repo.drools.file}") 
	String file;

	private KieFileSystem getKieFileSystem() throws IOException {
		
		try {
		    URL ruleUrl = new URL(url);
		    InputStream inputStream = ruleUrl.openStream();
		    byte[] bytes = IOUtil.toByteArray(inputStream);

		    KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
		    kieFileSystem.write(ResourceFactory.newByteArrayResource(bytes).setSourcePath(file));
		    
    		return kieFileSystem;
		    // use the kieBase to execute your rules
		} catch (Exception e) {
		    // handle exception
            System.out.println("ERROR: " + e.getMessage());	
            return null;
		}
		/*
		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
		//kieFileSystem.write(ResourceFactory.newClassPathResource("offer.drl"));
		kieFileSystem.write(ResourceFactory.newClassPathResource(url));
		
		return kieFileSystem;*/
	}

	/*0*/
	/*crea una memoria de trabajo con estado*/
	@Bean
	public KieContainer getKieContainer() throws IOException {
		System.out.println("Container created...");
		
		getKieRepository();
		
		KieBuilder kb = kieServices.newKieBuilder(getKieFileSystem());
		kb.buildAll();
		KieModule kieModule = kb.getKieModule();

	    KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
	    

		configKieScanner();
	    
		return kieContainer;

	}
/*2*/
	private void getKieRepository() {
		final KieRepository kieRepository = kieServices.getRepository();
		kieRepository.addKieModule(new KieModule() {
			public ReleaseId getReleaseId() {
				
				//return kieRepository.getDefaultReleaseId();
				return getReleaseID();
			}
		});
	}
/*1*/
    @Bean
    public KieSession getKieSession() throws IOException {
		System.out.println("session created...");
		KieContainer kcontainer = getKieContainer();
		
		/*
		// Create a KieScanner and start it with a 10 second polling interval
	    KieScanner kieScanner = kieServices.newKieScanner(kcontainer);
	    kieScanner.start(5000L);
	    //kieScanner.now();*/
	    
	    
		return kcontainer.newKieSession();

	}
    
    private void configKieScanner() {
    	
    	KieModuleModel kmodule = KieServices.Factory.get().newKieModuleModel();

    	KieBaseModel kieBaseModel1 = kmodule.newKieBaseModel("KBase2").addInclude("KBase1");
    	
    	//ReleaseId releaseId = kieServices.newReleaseId("com.example", "my-rules", "1.0.0");    	
    	KieContainer kieContainer = kieServices.newKieContainer(getReleaseID());    	
    	KieScanner kieScanner = kieServices.newKieScanner(kieContainer);
	    kieScanner.start(5000L);
    }
    
    private ReleaseId getReleaseID() {
    	ReleaseId releaseId = kieServices.newReleaseId("com.example", "my-rules", "1.0.0");    	
    	return releaseId;
    }
}
