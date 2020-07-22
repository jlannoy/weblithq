<#macro table objectName='' rows=[] align='center' id=objectName+'s' topLink='/'+objectName+'/edit/' topIcon='blue plus' modal=true noButtons=false view=false edit=false duplicate=false delete=false enable=false order=true selectable=true celled=true compact=true>
    <#assign objectName=objectName in t />
    <#assign id=id in t />
    <#assign defaultAlign=align in t />
    <#assign columns=[] />
    <#nested />
    <table id="${id}" class="ui ${selectable?then('selectable','')} ${celled?then('celled','')} ${compact?then('compact','')} ${defaultAlign} aligned table">
    <thead>
        <tr>
            <#list columns as column>
            <th class="${(column[2] != defaultAlign)?then(column[2]+' aligned','')} ${column[3]}">${column[1]}</th>
            </#list>
            <#if !noButtons>
            <th class="center aligned collapsing">
                <#if topLink != ''><#if modal><@modalButton topLink topIcon /><#else><@button topLink topIcon /></#if></#if>
            </th>
            </#if>
        </tr>
    </thead>
    <tbody>
        <#list rows as row>
        <tr class="${(row.disabled?? && row.disabled)?then('disabledModel','')}">
            <#list columns as column>
            <#assign row=row in t />
            <#assign value=(row[column[0]])!'' in t />
            <td class="${(column[2] != defaultAlign)?then(column[2]+' aligned','')}"><#if value?is_boolean><@layout.boolean value /><#elseif value?is_string && value == ''>-<#elseif column[5] != ''><@column[5]?interpret /><#elseif column[4] != ''>${(column[4]?eval)!""}<#else>${value}</#if></td>
            </#list>
            <#if !noButtons>
            <td class="center aligned collapsing buttons">
                <#if row.id??>
                <#if view><@button '/'+objectName+'/view/'+row.id 'blue zoom' /></#if>
                <#if edit && modal><@modalButton '/'+objectName+'/edit/'+row.id 'teal pencil' /></#if>
                <#if edit && !modal><@button '/'+objectName+'/edit/'+row.id 'teal pencil' /></#if>
                <#if duplicate && modal><@modalButton '/'+objectName+'/edit/'+row.id+'?duplicate=true' 'orange copy' /></#if>
                <#if duplicate && !modal><@button '/'+objectName+'/edit/'+row.id+'?duplicate=true' 'orange copy' /></#if>
                <#if order && row.orderable?? && row.orderable && !row?is_first><@button '/'+objectName+'/down/'+row.id 'yellow up arrow' true /></#if>
                <#if order && row.orderable?? && row.orderable && !row?is_last><@button '/'+objectName+'/up/'+row.id 'yellow down arrow' true /></#if>
                <#if enable && row.enableable?? && row.enableable><@button '/'+objectName+'/enable/'+row.id 'green check' true 'Confirmer l\'activation?' /></#if>
                <#if enable && row.disableable?? && row.disableable><@button '/'+objectName+'/disable/'+row.id 'red ban' true 'Confirmer la désactivation?' /></#if>
                <#if delete && row.deletable?? && row.deletable><@button '/'+objectName+'/delete/'+row.id 'red trash alternate' true 'Confirmer la suppression?' /></#if>
                </#if>
            </td>
            </#if>
        </tr>
        </#list>
    </thead>
    </table>
</#macro>

<#macro column name title align=defaultAlign size='' eval='' interpret=''>
    <#assign columns=columns+ [[name, title, align, size, eval, interpret]] />
</#macro>

<#macro button link icon post=false confirm=''>
<a class="ui mini icon basic button" href="${post?then('javascript:call(\''+link+'\')',link)}" <#if confirm != ''>onclick="return confirm('${confirm?js_string}')"</#if>><i class="${icon} icon"></i></a>
</#macro>

<#macro modalButton link icon>
<a class="ui mini icon basic button" href="javascript:showModalUrl('${link}')"><i class="${icon} icon"></i></a>
</#macro>

<#macro datatable ordered=0>
<script type="text/javascript">
 $(document).ready(function() {
  var table = $('#${id}').DataTable({
   language: datatablesFr,
   dom : datatableDomExport,
   stateSave: true, 
   stateDuration: 0,
   lengthMenu: datatableMenu,
   paging : true,
   pagingType : 'simple_numbers',
   order: [[ ${ordered}, 'asc' ]],
   columnDefs : [
    { 'targets' : -1, 'orderable' : false }
   ],
  });
  <#if flash.id??>table.page.jumpTo('${flash.id}', -1);</#if>
 });
</script>
</#macro>