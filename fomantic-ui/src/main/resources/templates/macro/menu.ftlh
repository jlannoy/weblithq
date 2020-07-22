<#macro navbar color='' inverted=true size='large' activeClass='active'>
    <#assign activeClass=activeClass in menu />
    <div class="ui fixed ${color} ${inverted?then('inverted','')} ${size} navbar menu">
        <#nested />
    </div>
</#macro>

<#macro title text='' logo='' link='/' width='auto' selectedIcon=selectedIcon!''>
    <a class="header logo item ${(selectedIcon == '')?then(activeClass,'')}" href="${contextPath}${link}"><#if logo != ''><img src="${contextPath}/assets/img/${logo}" style="margin-right: ${(text != '')?then(15,0)}px; width: ${width};" /></#if>${text}</a>
</#macro>

<#macro mobile title='Menu'>
    <div class="computer hidden large screen hidden ui inverted blue fixed centered menu">
        <a class="launch icon item" href="javascript:sidebar()">
            <i class="content icon"></i> &nbsp; Menu
        </a>
    </div>
</#macro>

<#macro right>
    <div class="right menu">
        <#nested />
    </div>
</#macro>

<#macro item icon label path dynamicLabel=false badge='' selectedIcon=selectedIcon!''>
    <a class="item ${(selectedIcon == icon)?then(activeClass,'')}" href="${(path?starts_with('javascript') || path?starts_with('http'))?then('',contextPath)}${path}" ${path?starts_with('http')?then('target="_blank"','')}>
        <i class="${icon} icon" style="${(label == '')?then('margin-right:0 !important;','')}"></i>
        <#if !dynamicLabel || selectedIcon == icon>${label}<#else><span class="content">${label}</span></#if>
        <#if badge?is_number && badge gt 0><div class="ui orange small label" style="position: absolute; top: 3px; right: 3px; padding: 4px">${badge}</div></#if>
    </a>
</#macro>

<#macro dropdown icon label dynamicLabel=false badge=''>
<div class="ui attached dropdown item">
    <i class="${icon} icon"></i> <#if !dynamicLabel>${label}<#else><span class="content">${label}</span></#if>
    <#if badge?is_number && badge gt 0><div class="ui orange small label" style="position: absolute; top: 3px; right: 3px; padding: 4px">${badge}</div></#if>
    <i class="dropdown icon"></i>
    <div class="ui mini menu">
        <#nested />
    </div>
</div>
</#macro>

<#macro search src itemId='navbarSearch' label='Rechercher...' blurLabel=label minChars=3 results=3 noResults=false>
    <div id="${itemId}" class="ui right aligned category search item">
        <div class="ui icon small transparent inverted input">
            <input class="prompt" type="text" name="value" placeholder="${label}" autocomplete="off">
            <i class="search icon"></i>
        </div>
        <div class="small results"></div>
    </div>
    <script type="text/javascript">
    $(document).ready(function() { 
        $('#${itemId}').search({
            type: 'category',
            minCharacters: ${minChars},
            maxResults: ${results},
            showNoResults: ${noResults?string},
            apiSettings: {
                url: '${contextPath}/${src}?value={query}'
            }
        });
        <#if label != blurLabel>
        $('#${itemId} input').focus(function() { $(this).attr('placeholder', '${blurLabel}') });
        $('#${itemId} input').blur(function() { $(this).attr('placeholder', '${label}') });
        </#if>
    });
    </script>
</#macro>