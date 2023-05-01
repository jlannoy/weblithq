#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.security;

import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import ${package}.domains.user.UserRole;

@ApplicationScoped
public class RolesAugmentor implements SecurityIdentityAugmentor {

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        return context.runBlocking(build(identity));
    }

    private Supplier<SecurityIdentity> build(SecurityIdentity identity) {

        if (identity.isAnonymous()) {
            return () -> identity;
        } else {
            QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder()
                    .setPrincipal(identity.getPrincipal())
                    .addAttributes(identity.getAttributes())
                    .addCredentials(identity.getCredentials())
                    .addRoles(identity.getRoles());

            if (identity.hasRole(UserRole.ADMIN)) {
                builder.addRole(UserRole.MANAGER);
                builder.addRole(UserRole.USER);
            }

            if (identity.hasRole(UserRole.MANAGER)) {
                builder.addRole(UserRole.USER);
            }

            // OIDC test
            // NOT WORKING : no db access inside IO thread

            //            if (identity.getPrincipal() instanceof JsonWebToken) {
            //                String email = ((JsonWebToken) identity.getPrincipal()).getClaim("email");
            //
            //                Optional<UserProfile> profile = UserProfile.find("email", email).firstResultOptional();
            //                profile.ifPresentOrElse(p -> {
            //                    builder.addAttribute("user.title", p.title);
            //                    builder.addAttribute("user.email", p.email);
            //                    builder.addRole(p.role);
            //                }, () -> {
            //                    throw new ForbiddenException("Unknown user");
            //                });
            //            }

            return builder::build;
        }

    }
}