package com.flying.common.annotation.handler;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.flying.common.annotation.Transaction;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceHandler;
import com.flying.framework.service.ServiceHandlerContext;

/**
 * 支持事务
 * 
 * @author king
 *
 */
public class TransactionHandler implements ServiceHandler<Transaction> {

	@Override
	public Data handle(Transaction annotation, Data request, ServiceHandlerContext context) throws Exception {
		final LocalModule module = context.getModule();
		PlatformTransactionManager txManager = module.getSpringBeanFactory().getBean(annotation.connectionTag());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(annotation.isolationLevel());
		def.setPropagationBehavior(annotation.propagationBehavior());
		TransactionStatus status = txManager.getTransaction(def);
		try {
			Data d = context.doChain(request);
			txManager.commit(status);
			return d;
		} catch (Exception e) {
			txManager.rollback(status);
			throw e;
		}
	}

}
