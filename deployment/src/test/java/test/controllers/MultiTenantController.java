package test.controllers;

import io.weblith.core.multitenancy.TenantContext;
import io.weblith.core.router.annotations.Controller;
import io.weblith.core.router.annotations.Get;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import javax.ws.rs.DefaultValue;

@Controller("/Tenant")
public class MultiTenantController {

    @Get
    public String id() {
        return TenantContext.id();
    }

    @Get
    public String domain() {
        return TenantContext.domain();
    }

}
