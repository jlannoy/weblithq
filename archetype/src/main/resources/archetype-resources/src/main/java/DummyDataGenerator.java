#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.util.Date;
import java.util.GregorianCalendar;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

import org.jboss.logging.Log.

import io.quarkus.runtime.StartupEvent;
import ${package}.domains.simpleEntity.SimpleEntity;
import ${package}.domains.simpleEntity.SimpleEntity.Type;
import ${package}.domains.user.User;
import ${package}.domains.user.UserRole;

@ApplicationScoped
public class DummyDataGenerator {

    private final static Log.Log.= Log.getLog.DummyDataGenerator.class);

    @Transactional
    public void saveDefaultData(@Observes StartupEvent startup) {

        if (SimpleEntity.count() == 0) {
            new SimpleEntity("First Entity", 5, new Date(), Type.A).persist();
            new SimpleEntity("Second Entity", 10, new GregorianCalendar(2020, 1, 2).getTime(), Type.B).persist();
            new SimpleEntity("Third Entity", 1, new Date(), Type.C).persist();
            Log.info("Default data persisted");
        }

        if (User.count() == 0) {
            User.add("a@a.com", "My Admin", "admin", UserRole.ADMIN);
            User.add("m@m.com", "My Manager", "manager", UserRole.MANAGER);
            User.add("u@u.com", "My User", "user", UserRole.USER);
            Log.info("Default users persisted");
        }

    }

}
