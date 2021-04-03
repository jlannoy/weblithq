package io.weblith.core.multitenancy;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import io.quarkus.arc.ContextInstanceHandle;
import io.quarkus.arc.InjectableBean;
import io.quarkus.arc.InjectableContext;
import io.quarkus.arc.impl.ContextInstanceHandleImpl;

public class TenantScopeInjectableContext implements InjectableContext {

	static final Map<String, Map<Contextual<?>, ContextInstanceHandle<?>>> TENANT_ACTIVE_SCOPE = new HashMap<>();

	@Override
	public Class<? extends Annotation> getScope() {
		return TenantScoped.class;
	}

	@Override
	public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
		Map<Contextual<?>, ContextInstanceHandle<?>> activeScope = TENANT_ACTIVE_SCOPE.get(TenantContext.id());
		if (activeScope == null) {
			throw new ContextNotActiveException();
		}

		@SuppressWarnings("unchecked")
		ContextInstanceHandle<T> contextInstanceHandle = (ContextInstanceHandle<T>) activeScope
				.computeIfAbsent(contextual, c -> {
					if (creationalContext == null) {
						return null;
					}
					T createdInstance = contextual.create(creationalContext);
					return new ContextInstanceHandleImpl<T>((InjectableBean<T>) contextual, createdInstance,
							creationalContext);
				});

		return contextInstanceHandle.get();
	}

	@Override
	public <T> T get(Contextual<T> contextual) {
		Map<Contextual<?>, ContextInstanceHandle<?>> activeScope = TENANT_ACTIVE_SCOPE.get(TenantContext.id());
		if (activeScope == null) {
			throw new ContextNotActiveException();
		}

		@SuppressWarnings("unchecked")
		ContextInstanceHandle<T> contextInstanceHandle = (ContextInstanceHandle<T>) activeScope.get(contextual);

		if (contextInstanceHandle == null) {
			return null;
		}

		return contextInstanceHandle.get();
	}

	@Override
	public boolean isActive() {
		return TENANT_ACTIVE_SCOPE.get(TenantContext.id()) != null;
	}

	@Override
	public void destroy(Contextual<?> contextual) {
		ContextInstanceHandle<?> contextInstanceHandle = TENANT_ACTIVE_SCOPE.get(TenantContext.id()).get(contextual);
		if (contextInstanceHandle != null) {
			contextInstanceHandle.destroy();
		}
	}

	/**
	 * Two methods below are specific to Quarkus because defined on
	 * InjectableContext.
	 */

	@Override
	public void destroy() {
		Map<Contextual<?>, ContextInstanceHandle<?>> context = TENANT_ACTIVE_SCOPE.get(TenantContext.id());
		if (context == null) {
			throw new ContextNotActiveException();
		}
		context.values().forEach(ContextInstanceHandle::destroy);
	}

	@Override
	public ContextState getState() {
		return new ContextState() {

			@Override
			public Map<InjectableBean<?>, Object> getContextualInstances() {
				Map<Contextual<?>, ContextInstanceHandle<?>> activeScope = TENANT_ACTIVE_SCOPE.get(TenantContext.id());

				if (activeScope != null) {
					return activeScope.values().stream()
							.collect(Collectors.toMap(ContextInstanceHandle::getBean, ContextInstanceHandle::get));
				}
				return Collections.emptyMap();
			}
		};
	}

}