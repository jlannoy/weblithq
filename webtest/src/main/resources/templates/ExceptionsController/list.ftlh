<@page.app title='Exceptions Manager' selectedIcon='bug'>
    <@layout.title />
    <p>WIP for accessing some exeption-throwing pages. Dependant on `jaxrsProviders` registered in `WeblithFreemarkerProcessor`.</p>
    <ul class="ui list">
        <li><a href="${contextPath}/Exceptions/none">Not existing page</a></li>
        <li><a href="${contextPath}/Exceptions/web404">WebApplicationException with 404 status code</a></li>
        <li><a href="${contextPath}/Exceptions/illegal">IllegalArgumentException with message</a></li>
    </ul>
</@page.app>