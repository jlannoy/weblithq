<#-- Here we set up a first main template for our app, with a top level navbar  -->
<#macro app title selectedIcon=''>
    <#assign selectedIcon=selectedIcon in menu>
    <#assign pageTitle=title in layout />
    <#assign pageMeta in layout><@layout.defaultMeta /></#assign>
    <#assign bodyHeader in layout><@appMenu /></#assign>
    <#assign containerHeader in layout><@layout.flashMessages /></#assign>
    <@layout.template>
        <#nested />
    </@>
</#macro>

<#-- The top level navbar of our app template -->
<#macro appMenu>
    <@menu.navbar color='blue' size='huge'>
        <@menu.title text=i18n('menu.title') width='3.9em' link='/' />
        <@menu.item 'users' 'Users' '/User/list' />
        <@menu.item 'list' 'Entities' '/SimpleEntity/list' />
        <@menu.item 'user lock' 'Restricted' '/RestrictedAccess/list' />
        <@menu.item 'bug' 'Exceptions' '/Exceptions/list' />
        
        <@menu.right>
            <@menu.dropdown 'user' Identity.principal.name>
                <@menu.item 'flag' 'In English' requestPath+'?lang=en' />
                <@menu.item 'flag' 'En Français' requestPath+'?lang=fr' />
                <#if Identity.anonymous><@menu.item 'lock' i18n('menu.login') '/auth/login' />
                <#else><@menu.item 'unlock' i18n('menu.logout') '/auth/logout' /></#if>
            </@menu.dropdown>
        </@menu.right>
    </@menu.navbar>
</#macro>

<#-- Secondary template for our app, allowing to display kind of a landing page. -->
<#macro board title subtitle=''>
    <#assign pageTitle=title in layout>
    <#assign pageMeta in layout>
        <@layout.defaultMeta />
        <@boardStyle />
    </#assign>
    <#assign containerHeader in layout>
        <h1 class="ui center aligned inverted header">
            ${title}
            <#if subtitle != ''><div class="sub header">${subtitle}</div></#if>
        </h1>
    </#assign>
    <@layout.template>
        <#nested />
    </@>
</#macro>

<#-- Examplae CSS style dedicated to our board template. It could of course also be put in a separate asset. -->
<#macro boardStyle>
<style scoped>
    body {
        background-color: #8EC5FC;
        background-image: linear-gradient(62deg, #8EC5FC 0%, #E0C3FC 100%);
    }
    h1.ui.header {
        font-size: 4em;
    }
    h1.ui.header .sub.header {
        font-size: .5em;
    }
    ui.white.buttons .button, .ui.white.button {
        background-color: #FFFFFF;
        color: rgba(0,120,122,1);
        text-shadow: none;
    }
    a.fluid.card:hover .ui.button {
        background-color: #CACBCD;
        color: rgba(0, 0, 0, 0.8);
    }
</style>
</#macro>