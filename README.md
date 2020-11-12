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

The current status of the project is a **Preview Release**. Several *existing* code parts of the original Weblith project must still be migrated.

1. *Extensive documentation* : Each Weblith concept should be documented appart with more details and configuration properties.
1. *UI components documentation* : The goal is to analyze the relavance of `Storybook` - allowing serverside components since last version - for showcasing all Weblith UI components.
1. *Router* : In the original project, there is a central Router allowing for example reverse routing in templates.
1. *Multitenancy* : domain management + dynamic tenant configuration
1. *Unit tests* : a lot of unit tests must still be reused

