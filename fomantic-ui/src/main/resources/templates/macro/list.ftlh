<#macro labels pointingLabels=false class=''>
<#assign pointingLabels=true in list />
<div class="ui fixed labels ${class} item list" style="margin-top: 0;">
    <#nested />
</div>
</#macro>

<#macro item label value='' color='' interpret='' icon='' pointing=pointingLabels id=''>
<div id='${id!''}' class="item">
    <#if label != ''><div class="ui ${pointing?then('right pointing','')} ${color} fixed label"><#if icon != ''><i class="${icon} icon"></i></#if>${label}</div>
    <#elseif icon != ''><div class="ui ${pointing?then('right pointing','')} ${color} icon label"><i class="${icon} icon"></i></div></#if>
    <#if interpret != ''><@interpret?interpret /><#elseif !value?is_string || value != ''><span class="ui ${color} text">${value}</span><#else><#nested /></#if>
</div>
</#macro>