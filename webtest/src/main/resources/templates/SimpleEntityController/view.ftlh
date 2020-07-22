<@page.app title=SimpleEntity.name selectedIcon='list'>
    
    <@layout.title />
    <@tabs.horizontal values=['Formatted','Text','Third'] style1='top attached' style2='segment' ; Menu, Index>
        <#switch Index>
            <#case 0><@formattedTab /><#break>
            <#case 1><@textTab /><#break>
            <#case 2><@thirdTab /><#break>
        </#switch>
    </@>

</@>

<#macro formattedTab>
    <p>Using the <i>@list</i> weblith fomantic-ui components.</p>
    <@list.labels>
        <@list.item label='Quantity' value=SimpleEntity.quantity color=(SimpleEntity.quantity == 1)?then('red','green') />
        <@list.item label='Quantity 2' value=SimpleEntity.quantity color=(SimpleEntity.quantity == 1)?then('orange','blue') />
        <@list.item label='Date' value=(prettyTime(SimpleEntity.date)) />
        <@list.item label='Type' value=i18n('enum.Type.' + SimpleEntity.type) />
    </@>
</#macro>

<#macro textTab>
    <p>Quantity : ${SimpleEntity.quantity!'-'}</p>
    <p>Quantity 2 : ${SimpleEntity.quantity2!'-'}</p>
    <p>Date : ${(SimpleEntity.date?string['dd/MM/yyyy'])!'-'}</p>
    <p>Type : ${SimpleEntity.type!'-'}</p>
</#macro>

<#macro thirdTab>
    <p>Third tab...</p>
</#macro>