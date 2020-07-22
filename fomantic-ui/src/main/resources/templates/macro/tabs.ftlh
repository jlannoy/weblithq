<#macro horizontal values titleKey='' idKey='' active='' numbered=false style1='pointing secondary' style2=''>
<div class="ui ${style1} tabular menu">
    <#list values as Value>
    <a class="item ${tab_active(Value,titleKey,idKey,active,Value?is_first)?then('active','')}" data-tab="${tab_id(Value,idKey,titleKey)}">
        <#if numbered><span style="color: #CCC;">${Value?index+1}. </span> </#if>
        ${tab_title(Value,titleKey)}
    </a>
    </#list>
</div>
<#list values as Value>
<div class="ui bottom attached ${style2} ${tab_active(Value,titleKey,idKey,active,Value?is_first)?then('active','')} tab" data-tab="${tab_id(Value,idKey,titleKey)}">
    <#nested Value Value?index>
</div>
</#list>    
</#macro>

<#macro vertical values titleKey='' idKey='' active='' numbered=false wide1='four' wide2='twelve'>
<div class="ui grid">
    <div class="${wide1} wide column" style="padding-right: 0">
        <div class="ui vertical fluid tabular menu">
            <#list values as Value>
            <a class="tab item ${tab_active(Value,titleKey,idKey,active,Value?is_first)?then('active','')}" data-tab="${tab_id(Value,idKey,titleKey)}">
                <#if numbered><span style="color: #CCC;">${Value?index+1}. </span> </#if>
                ${tab_title(Value,titleKey)}
            </a>
            </#list>
        </div>
    </div>
    <div class="${wide2} wide stretched column">
        <#list values as Value>
        <div class="ui tab ${tab_active(Value,titleKey,idKey,active,Value?is_first)?then('active','')}" data-tab="${tab_id(Value,idKey,titleKey)}">
            <#nested Value Value?index>
        </div>
        </#list>
    </div>
</div>   
</#macro>

<#macro links withDivider=true style='margin-bottom: 3px;'>
<div class="ui secondary menu" style="${style}">
    <#nested />
</div>
<#if withDivider><div class="ui heavy divider" style="margin-top: 0px;"></div></#if>
</#macro>

<#macro link name link active=false icon='' itemClass='' number=''>
    <a class="item ${itemClass} ${active?then('active','')}" href="${link}"><#if number != ''><span style="color: #CCC;">${number}. </span> </#if><#if icon != ''><i class="${icon} icon"></i> </#if>${name}</a>
</#macro>

<#function tab_id Value idKey titleKey>
  <#return (idKey == '')?then(tab_title(Value,titleKey)?replace(' ','_'),Value[idKey])>
</#function>

<#function tab_title Value titleKey>
  <#return (titleKey == '')?then(Value,Value[titleKey])>
</#function>

<#function tab_active Value titleKey idKey active first>
  <#return (active == '')?then(first,(idKey == '')?then(active == tab_title(Value,titleKey),active == Value[idKey]))>
</#function>