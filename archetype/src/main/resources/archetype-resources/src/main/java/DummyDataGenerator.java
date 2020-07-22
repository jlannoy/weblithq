#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.util.Date;
import java.util.GregorianCalendar;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;
import ${package}.domains.simpleEntity.SimpleEntity;
import ${package}.domains.simpleEntity.SimpleEntity.Type;
import ${package}.domains.user.User;
import ${package}.domains.user.UserRole;

@ApplicationScoped
public class DummyDataGenerator {

    private final static Logger LOGGER = Logger.getLogger(DummyDataGenerator.class);

    @Transactional
    public void saveDefaultData(@Observes StartupEvent startup) {

        if (SimpleEntity.count() == 0) {
            new SimpleEntity("First Entity", 5, new Date(), Type.A).persist();
            new SimpleEntity("Second Entity", 10, new GregorianCalendar(2020, 1, 2).getTime(), Type.B).persist();
            new SimpleEntity("Third Entity", 1, new Date(), Type.C).persist();
            LOGGER.info("Default data persisted");
        }

        if (User.count() == 0) {
            User.add("a@a.com", "My Admin", "admin", UserRole.ADMIN);
            User.add("m@m.com", "My Manager", "manager", UserRole.MANAGER);
            User.add("u@u.com", "My User", "user", UserRole.USER);
            LOGGER.info("Default users persisted");
        }

    }

}
