![Java Build](https://github.com/weblith/weblith/workflows/Java%20Build/badge.svg) 

# Weblith.io
> Quarkus-powered Java Web Application Framework

**Current version** : 0.1.0. A preview release made for the Quarkus Hackathon. *You can use it as a showcase of what Weblith could be, but at this time is still too experimental to start a production target project.*

**Quarkus Hackathon category winner !!** Follow us on [twitter](https://twitter.com/weblith_io) for more updates about this projet.

## How to use it

A Maven archetype exists, so that you can easily bootstrap a Weblith showcase application. Run this method :

```
mvn archetype:generate                      \
  -DarchetypeGroupId=net.zileo              \
  -DarchetypeArtifactId=weblith-archetype   \
  -DarchetypeVersion=0.1.0                  \
  -Dversion=1.0.0-SNAPSHOT                  \
  -DgroupId=org.acme                        \
  -DartifactId=my-weblith
```

Then you should be able to `cd my-weblith` and run `mvn compile quarkus:dev`.

## Roadmap

Missing features or implementations:
1. Session scope encryption
1. UI components documentation + showcase
   
## Original project

There are some features that could still be bringed back from the original project. Like: 
1. *Router* : Central Router allowing for example reverse routing in templates.
1. *TenantConfiguration* : dynamic tenant configuration
1. *Flyway tenants* : classes allowing migrations regarding configured tenants

