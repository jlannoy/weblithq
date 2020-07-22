<@page.app title='Restricted Access Pages' selectedIcon='user lock'>
    <@layout.title />
    <p>You can try to access some protected pages :</p>
    <ul class="ui list">
        <li><a href="${contextPath}/RestrictedAccess/auth">Authenticated page</a></li>
        <li><a href="${contextPath}/RestrictedAccess/admin">Role-based page : Admin</a></li>
        <li><a href="${contextPath}/RestrictedAccess/manager">Role-based page : Manager</a></li>
        <li><a href="${contextPath}/RestrictedAccess/user">Role-based page : User</a></li>
    </ul>
    <@layout.divider />
    <p><b>Current : </b><span class="ui red text">${state!"List of all pages"}</span></p>
    <#if Identity.anonymous>
        <p><i>Not authenticated</i></p>
    <#else>
        <p><b>Roles : </b><span class="ui green text">${Identity.roles?join(", ")}</span></p>
        <p><b>Principal name : </b>${Identity.principal.name!'-'}</p>
        <p><b>Principal object : </b>${Identity.principal}</p>
        <p><a href="${contextPath}/auth/logout">Logout</a></p>
    </#if>
</@page.app>