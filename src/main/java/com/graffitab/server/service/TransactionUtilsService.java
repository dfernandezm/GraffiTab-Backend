package com.graffitab.server.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class TransactionUtilsService {
	
	@Resource
    private PlatformTransactionManager transactionManager;
	
    private TransactionTemplate requiresNewTransactionTemplate;
    
    private TransactionTemplate transactionTemplate;
    
	@PostConstruct
    public void init() throws Exception {
        requiresNewTransactionTemplate = new TransactionTemplate(transactionManager);
        requiresNewTransactionTemplate.setPropagationBehavior(Propagation.REQUIRES_NEW.value());
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

	public void executeInTransaction(Runnable runnable) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
	        @Override
	        protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
	            runnable.run();
	        }
	    });
	}
	
	public void executeInNewTransaction(Runnable runnable) {
		requiresNewTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
	        @Override
	        protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
	            runnable.run();
	        }
	    });
	}
}
