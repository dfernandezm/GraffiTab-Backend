package com.graffitab.server.service;

import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class TransactionUtils {

	private static Logger LOG = LogManager.getLogger();

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

	public <T> T executeInTransactionWithResult(Callable<T> callableWithResult) {
		return transactionTemplate.execute(new TransactionCallback<T>() {

			@Override
			public T doInTransaction(TransactionStatus status) {
				try  {
					return callableWithResult.call();
				} catch (Throwable t) {
					LOG.error("Error executing transaction with result", t);
					throw new RuntimeException(t);
				}
			}
		});

		// This has a problem sometimes when doing an insert to the database. Need to investigate.
//		return transactionTemplate.execute((transactionStatus) -> {
//			try  {
//				return callableWithResult.call();
//			} catch (Throwable t) {
//				LOG.error("Error executing transaction with result", t);
//				throw new RuntimeException(t);
//			}
//		});
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
