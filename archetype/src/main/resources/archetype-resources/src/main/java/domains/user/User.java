#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domains.user;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.annotations.NaturalId;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

@Entity
@UserDefinition
public class User extends PanacheEntity {

    @NaturalId
    @NotBlank
    @Email
    @Username
    public String email;

    public String title;

    @NotBlank
    @Password
    public String password;

    @Roles
    public String role;

    public static void add(String email, String title, String password, String role) {
        User user = new User();
        user.email = email;
        user.title = title;
        user.password = BcryptUtil.bcryptHash(password);
        user.role = role;
        user.persist();
    }

}