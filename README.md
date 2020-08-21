# Weblith.io
> Quarkus-powered Java Web Application Framework

**Current version** : 0.1.0. A preview release made for the Quarkus Hackathon. *You can use it as a showcase of what Weblith could be, but at this time is still too experimental to start a production target project.*


**Quarkus Hackathon category winner !!** Follow us on [twitter](https://twitter.com/weblith_io) for more updates about this projet.

## Table of content

 * [A little bit of history](#a-little-bit-of-history)
 * [Routes & Results](#routes-and-results)
 * [Request Context](#request-context)
 * [Forms Management](#forms-management)
 * [Freemarker Templates](#freemarker-templates)
 * [Fomantic UI library](#fomantic-ui-library)
 * TL;DR [HOW TO USE IT](#how-to-use-it)
 * [Authentication](#authentication)
 * [Roadmap](#roadmap)
  

## A little bit of history

### First version of Weblith

At its early stage, Weblith.io is an internal framework developed at [Zileo.net](https://zileo.net) derived from other frameworks like [Spring Boot](https://spring.io/), [Ninja Framework](http://www.ninjaframework.org/), [MangooIO](https://github.com/svenkubiak/mangooio). The goal is to provide more . Technically, it is based on stable well-known libraries : Google Guice, Lightbend Config, Undertow, Hibernate ORM, Hibernate Validator, Jackson, Freemarker... Q3-2020 the plan was open source it, by creating the missing documentation and archetype, and make it publicly available through [Weblith.io](http://weblith.io).

### Weblith goal

Weblith has been made to offer a quick and easy way to create **Server Side Rendered** web applications, focusing on developer joy by opinionated decisions allowing to minimize the developer tools needed, reduce boiler plate code, and even offer nice UI components. It uses stable and well-know libraries to avoid a long learning curve. It will produces one single Jar file with an embedded HTTP server that can run with few resources. Not that it doesn't reinvent the *monolithic* applications ; depending on the size of your project you should still use micro or external services when it makes sense, even with a front end application made using Weblith.

### So... Why now *Quarkus powered* ?

Following news on the Java battlefield, it was very interesting to see [Quakus](https://quarkus.io) and [Micronaut](http://micronaut.io) emerge. One problem we still wanted to fix with Weblith was the startup time (mainly slowed by Guice and Hibernate). With the **Quarkus Global Hackathon** launched, there was a good opportunity to try migrating some Weblith layers to Quarkus.

**Important Note** By this time (July 2020), you are reading a **condensed** documentation of a **Preview Release**, that have been succesfully set up for the Hackathon. If you're interested in this projet, see the Roadmap chapter for more information. Please note that - as requested by the Hackathon rules - no update or issue will be adressed before the end of the Hackathon.

## Routes and Results

### Resteasy integration

On a purely Quarkus point of view, Weblith works along with `quarkus-resteasy`. It will automatically register JAX-RS endpoints having to `GET` and `POST` resources. It also means that you can mix Reasteasy code with Weblith.

### Declaring routes

To declare new routes for your application, you only need to know three annotations : `@Controller`, `@Get` and `@Post`. Each annotation can have a value to represent the corresponding `path`, or it will be taken automaticaly from the class name (minus a `Controller` suffix) or method name.

```java
@Controller
class MyFirstController {

    @Get
    public Response myPage() { ... }
    
    @Get
    public Response myPage2(@PathParam String id) { ... }
    
    @Post
    public Response myAction() { ... }
    
}
```

Will be recognized as `/MyFirst/myPage`, `/MyFirst/myPage2/{id}`,  (GET) and `/MyFirst/myAction` (POST). `Response` is the standard JAX-RS returned object ; you'll see later that Weblith will help you on that part too.

```java
@Controller("/Controller")
class MySecondController {

    @Get("/page")
    public Response myPage() { ... }
    
    @Get("/page2/{id}")
    public Response myPage2(@PathParam("id") String identifier) { ... }
    
    @Post("/action")
    public Response myAction() { ... }
    
}
```

Will be recognized as `/Controller/page`, `/Controller/page/{id}` (GET) and `/MyFirst/action` (POST).

### Route parameters

There is no specific annotation for managing parameters of your routes. As you may have noticed on the previous examples, you can use the standard JAX-RS ones : `@PathParam`, `@QueryParam`, `@HeaderParam`, `@MatrixParam`, `@CookieParam` and `@FormParam`. Be sure to import the ones from `org.jboss.resteasy.annotations.jaxrs` package so that the `name` of each one is optional (taken from the method parameter name by default). `@DefaultValue` is also available.

### Route results

While you can configure yourself your JAR-RS `Response` objects, Weblith offers an handy way to manage the results of your routes via different `Result` objects : 
 * `TextResult` to render plain text
 * `RawResult` to render bytes
 * `StreamResult` to render a `File` or an `URL`
 * `JsonResult` to render some JSON
 * `HtmlResult` will be addressed in the [Freemarker Templates](#freemarker-templates) chapter
 
Result classes have some shortcut methods to configure the future response, like adding Cookies and changing the charset (`UTF-8` by default). You can also define your own custom `Result` classes if needed.

```java
    @Get
    public Result myPage() {
        return new TextResult("Hello World").charset(StandardCharsets.UTF_8);
    }
```

One last very import one is the `Redirect` result. All your POST actions will be CSRF-protected (unless marked with @NotCsrfProtected). So GET methods should never be used to modify things ; and successful POST actions should never directly render results, but rather redirect to a GET method, with some optional information. The `Redirect` result will help you this way.

```java
    @Post
    public Result doIt() {
        return new Redirect("/Test/myPage").withSuccess("You did it !");
    }
```

## Request Context

While in a controller or any other internal service, you can inject a `RequestContext` instance. This class is a center piece for all request related concepts, and give you access to : 

* `SessionScope` : a client-side cookie-based session (server-side session not supporter by Weblith)
* `FlashScope` : a specific cookie-based scope that keep data from one request to an other
* `LocaleHandler` : a handler that can detect the current user-defined locale
* `seed()` and `get` : a map for storing some objects during the whole request execution
* `HttpRequest` and `UriInfo` : JAX-RS request information

## Forms Management

### Form submission

While you can use the standard `@Form` JAX-RS way for getting form information, and the `@Valid` to throws `ConstraintViolationException`, Weblith offers a nicer way to manage your forms. Simply embed your form object in a `Form` parameter like this :

```java
    @Transactional
    @Post
    public Result save(Form<SimpleEntity> form) {
        SimpleEntity dto = form.getValue();
        ... map dto field to real entity instance
        ... save entity
        return new Redirect("/SimpleEntity/list");
    }
```

The `SimpleEntity` object will be filled with your submitted data. It uses a Jackson `ObjectMapper` underneath that maps a `Map` of all parameters to the required object.

### Form validation

`Form` will allow you to trigger the validation of the filled object. All validation (but also parameter conversion) error messages will then be available as a list of `Violation` objects. Welbith UI components will handle those messages.

```java
    @Transactional
    @Post
    public Result save(Form<SimpleEntity> form) {
        if (!form.validate()) {
            return formTemplate.render(form);
        }
        ...
    }
```

## Freemarker Templates

### Why Freemarker ?

During our migration to Quarkus, we tried to reimplement our Freemarker components with Qute, the template engine proposed but the Quarkus team. But it was lacking some features needed for it. As the components base was already well-defined and stable, the choice have been made to continue using Freemarker. **The downsides will be the lack of reactive support (but not intended for Weblith) and the native build support that seems hard to get working.**

### Rendering templates

Simply return a `HtmlResult` to render Freemarker template. This result must point to a template name, and a directory (or the controller name, by default).

```java
    @Get("/")
    public HtmlResult home() {
        return new HtmlResult("Main", "home").render("MyData","MyValue");
    }
```

This will render the template `templates/Main/home.ftlh` (stored in the classpath, so in `src/main/resources`), with a `MyData` available variable. Some default variables are always added by Weblith :

 * `hostname`, `requesPath` and `params` : relative to the current request
 * `contextPath` : value of `quarkus.http.root-path`
 * `lang` : current used defined language
 * `flash` : map with current Flash scope data

Refer to the [Freemarker](https://freemarker.apache.org/docs/index.html) documentation for more information about how to use this engine.

### Injecting templates

A better way to use templates is to inject instances of `FreemarkerTemplate` in the controllers. This way, template paths are checked at build-time.

```java
@Controller
public class SimpleEntityController {

    @Inject
    FreemarkerTemplate list;
    
    @Get
    public HtmlResult list() {
        return list.render("SimpleEntities", SimpleEntity.listAll());
    }
    
}
```

The path of the template will automatically set to `templates/SimpleEntityController/list.ftlh`. It can be changed by using the `@TemplatePath` annotation.

### Internationalization support

`quarkus.locales` can be set to the locales supported by the application, and `quarkus.default-locale` to the default one. Then `messages.properties` files can be configure in a `i18n` directory (stored in the classpath, so in `src/main/resources`). A Weblith `LocaleHandler` will be used to detect the right language to use, but can be switched by passing a `lang` parameter to any request (can be changed via `quarkus.weblith.switchLanguageParam`).

While a `Messages` interface can be injected in any controller of internal service, two methods are also available in the templates :
 * `i18n(key,param1,param2,...)` : translates the given key
 * `prettyTime(date)` : displays date relatively

## Fomantic UI library

### Fomantic UI templating

One third Quarkus extension, that will be your real time-saver for building web applications, is a set of Freemarker tags that will allow you to quickly have a nice UI that fits with your Weblith backend code. The archetype you can use to boostrap your application will even offer a primary structure for your own base templates.

### Fomantic UI components

Sadly, this documentation draft do not enter into each component details (too early). But take a quick look at the two following example, as they are pretty clear on what they will produce.

```html
<@page.app title='User list' selectedIcon='users'>
    <@layout.title />
    <@t.table rows=Users align='left' noButtons=true>
        <@t.column name='role' title='Role' align='center' />
        <@t.column name='title' title='Title' />
        <@t.column name='email' title='E-mail' interpret='<@layout.mailto value />' />
    </@t.table>
</@page.app>
```

```html
<@f.form 'SimpleEntity' SimpleEntity.id>
    <@f.text name='name' label='Name' required=true />
    <@f.text name='quantity' label='Quantity' type='number' />
    <@f.calendar name='date' label='Date' />
    <@f.buttonBar>
        <@f.cancel />
        <@f.submit />
    </@>
</@>
```

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

## Authentication

The archetype immediately work with `quarkus-security-jpa`. A set of different User with roles will be created at startup and be available for login through the `FormAuthenticationMechanism` provided by Quarkus. Weblith only supplies the corresponding front end paths and login page. It should also integrates well with other security extensions available for Quarkus ; you'll find in the archetype `application.properties` an example block defining an OIDC connection to Auth0.

Thats means you can protect your pages via properties configuration or `@RolesAllowed` annotations, like any other Quakrus application. The archetype provide a showcase page for this part. Getting back relevant `SecurityIdentify` information must still be analyzed.

## Roadmap

### Future plans

As said before, the current status of the project is a **Preview Release**. Several *existing* code parts of the original Weblith project must still be migrated.

1. *Extensive documentation* : Each Weblith concept should be documented appart with more details and configuration properties.
1. *UI components documentation* : The goal is to analyze the relavance of `Storybook` - allowing serverside components since last version - for showcasing all Weblith UI components.
1. *Router* : In the original project, there is a central Router allowing for example reverse routing in templates.
1. *Multitenancy* : domain management + dynamic tenant configuration
1. *Unit tests* : a lot of unit tests must still be reused

