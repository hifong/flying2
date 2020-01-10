package com.flying.common.remote.hessian;

import com.flying.framework.security.Principal;

/**
 * @author wanghaifeng
 *
 */
public interface RemoteService {
	RemoteRequest invoke(Principal principal, String moduleId, String serviceId, RemoteRequest request) throws Exception;
}
