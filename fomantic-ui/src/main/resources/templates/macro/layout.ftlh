<#macro template>
<!DOCTYPE html>
<html lang="${lang!''}">
    <head>
        <title>APP - ${pageTitle!''}</title>
        ${pageMeta!''}
    </head>
    <body>
        ${bodyHeader!''}
        <div class="ui main text container" style="${containerStyle!''}">
            ${containerHeader!''}
            <#nested />
            ${containerFooter!''}
        </div>
        ${bodyFooter!''}
    </body>
</html>
</#macro>

<#macro defaultMeta robots='noindex, nofollow' charset='utf-8' favicons=false>
    <meta name="robots" content="${robots}">
    <meta name="googlebot" content="${robots}">
    <meta charset="${charset}" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
    
    <#if favicons>
    <link rel="icon" type="image/png" sizes="32x32" href="${assetsAt('favicon-32x32.png')}">
    <link rel="icon" type="image/png" sizes="16x16" href="${assetsAt('favicon-16x16.png')}">
    <link rel="shortcut icon" href="${assetsAt('favicon.ico')}">
    </#if>
    
    <#-- 
    <script src="${contextPath}/webjars/jquery/jquery.min.js"></script>
    <script src="${contextPath}/webjars/fomantic-ui/semantic.js"></script>
    <link rel="stylesheet" type="text/css" href="${contextPath}/webjars/fomantic-ui/semantic.css">
    -->
    
    <script src="https://cdn.jsdelivr.net/npm/jquery@3.3.1/dist/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/fomantic-ui@2.8.3/dist/semantic.css">
    <script src="https://cdn.jsdelivr.net/npm/fomantic-ui@2.8.3/dist/semantic.js"></script>
    <script src="${contextPath}/assets/js/weblith.js"></script>
    <link rel="stylesheet" type="text/css" href="${contextPath}/assets/css/weblith.css">
    
    <script>function call(url){$('<form />',{action:''+url,method:'POST',style:'display: none;'}).append('<authenticityForm/>').appendTo('body').submit();}</script>
    <#-- Don't forget that the href="call(...)" needs 2x encoding (?url?url), due to browser link interpretation
    https://stackoverflow.com/questions/1650676/why-does-26-get-decoded-to-when-passed-as-a-parameter-to-a-javascript-functio -->
</#macro>

<#macro helper>
<div class="ui helper modal">
    <div class="content"></div>
</div>
</#macro>

<#macro divider text='' heavy=false>
<div class="ui ${heavy?then('heavy','')} ${(text != '')?then('horizontal','')} divider">${text}</div>
</#macro>

<#macro title text=pageTitle!'' sub='' restricted=false helper='' strike=false>
<h2 class="ui dividing header ${strike?then('striked','')}">
    ${text}
    <#-- if restricted><@locked /></#if -->
    <#if helper != ''><span style="float: right; margin-left: 10px;"><a href="${contextPath}/help/${helper}" class="helper"><i class="green help circle icon"></i></a></span></#if>
    <#if sub != ''><span class="sub header ${strike?then('striked','')}" style="float: right; padding-top: 8px;">${sub}</span></#if>
</h2>
<#if helper != ''><@layout.helper /></#if>
</#macro>

<#macro nl2br text='-'>${text?replace("\n", '<br/>')?replace("\\*\\*(.*?)\\*\\*","<b>$1</b>","r")?replace("__(.*?)__","<i>$1</i>","r")?no_esc}</#macro>

<#macro badge value color='orange'><span class="ui ${color} circular label">${value}</span></#macro>

<#macro mailto value='' link=value default='-'><#if value == ''>${default}<#else><a href="mailto:${link}">${value}</a></#if></#macro>

<#macro tel value='' default='-'><#if value == ''>${default}<#else><a href="tel:${value}">${value}</a></#if></#macro>

<#macro boolean value=false><#if value><i class="green check icon"></i><#else><i class="red remove icon"></i></#if></#macro>

<#macro flashMessages>
	<#if flash.error??><@message 'negative' 'ban' 'common.message.header.error' flash.error /></#if>
	<#if flash.success??><@message 'positive' 'checkmark' 'common.message.header.success' flash.success /></#if>
    <#if flash.warning??><@message 'yellow' 'warning' 'common.message.header.warning' flash.warning /></#if>
</#macro>

<#macro message class='' icon='' header='' content=''>
    <div class="ui ${(icon != '')?then('icon','')} ${class} message">
        <#if icon != ''><i class="${icon} icon"></i></#if>
        <div class="content">
            <#if header != ''><div class="header">${i18n(header)}</div></#if>
            <#if content != ''><p>${content?no_esc}</p></#if>
        </div>
    </div>
</#macro>

<#macro cards class='four stackable'>
<div class="ui ${class} cards">
    <#nested />
</div>
</#macro>

<#macro card emoji='' icon='' label='' link='#'>
  <a class="ui fluid card" href="${link}">
    <div class="content">
      <div class="center aligned">
        <#if emoji != ''><em data-emoji="${emoji}" class="medium"></em><#elseif icon != ''><i class="huge ${icon} icon"></i><#else><#nested></#if>
      </div>
    </div>
    <div class="ui bottom attached button">${label}</div>
  </a>
</#macro>