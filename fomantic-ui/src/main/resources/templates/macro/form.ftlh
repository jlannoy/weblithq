<#macro form objectName='' objectId='' action='/'+objectName+'/save' validate=true segment=true size=''>
    <#assign formId=objectName+'Form' />
    <#if .vars[formId]??><#assign Form=.vars[formId] /><#assign FormData=.vars[objectName] /></#if>
    <div class="${params.embed???then('content',segment?then('ui segment',''))}">
        <@violations />
        <form id="${formId}" action="${contextPath}${action}" method="POST" class="ui ${size} form" ${validate?then('','novalidate')}>
            <input type="hidden" name="id" value="${objectId}" />
            <@authenticityHiddenField />
            <#nested />
        </form>
    </div>
</#macro>

<#macro violations>
    <#if Form?? && Form.hasViolations()>
    <div class="ui icon negative message">
        <i class="warning circle icon"></i>
        <div class="content">
            <div class="header">${i18n('common.message.header.validation')}</div>
            <ul class="list">
                <#list Form.violations as violation>
                <li>${i18n(violation)} <#if violation.fieldKey??><i>(${violation.fieldKey})</i></#if></li>
                </#list>
            </ul>
        </div>
    </div>
    </#if>
</#macro>

<#macro buttons>
    <#if params.embed??><div class="actions">
    <#else><@layout.divider /></#if>
        <#nested />
    <#if params.embed??></div></#if>
</#macro>

<#macro buttonBar heavy=false grid=true withDivider=true>
    <#if grid><div class="ui grid"></#if>
        <div class="sixteen wide center aligned column" style="${withDivider?then('padding-top: 0;','')}">
            <#if withDivider><@layout.divider '' heavy /></#if>
            <#nested />
            &nbsp; <img class="loadingImage" src="${contextPath}/assets/img/loader.gif" height="28" style="vertical-align: middle; visibility: hidden;" />
        </div>
    <#if grid></div></#if>
</#macro>

<#macro button url='' label='Save' icon='save' class='' confirm='' target=''>
    <a class="ui ${class} ${(icon != '')?then('icon','')} button" ${(target != '')?then('target="'+target+'"','')} href="${(link?starts_with('javascript') || link?starts_with('http'))?then('',contextPath)}${link}" <#if confirm != ''>onClick="return confirm('${confirm?js_string}');"</#if>>
        <#if icon != ''><i class="${icon} icon"></i></#if> ${label}
    </a>
</#macro>

<#macro cancel url='' label='Cancel' icon='undo' class='' confirm=''>
    <#assign link=params.embed???then('javascript:hideModal()', url) />
    <@button link label icon class confirm />
</#macro>

<#macro submit label='Save' icon='save' class='primary' action='' confirm='' embed=params.embed??>
    <#-- if !embed>
        <button type="submit" name="action" value="${action}" class="ui ${class} ${(icon != '')?then('icon','')} submit button" <#if confirm != ''>onClick="return confirm('${confirm?js_string}');"<#else>onclick="javascript:show('.loadingImage')"</#if>><#if icon != ''><i class="${icon} icon"></i></#if> ${label}</button>
    <#else -->
        <button type="submit" name="action" value="${action}" class="ui ${class} ${(icon != '')?then('icon','')} submit button" onClick="javascript:submit('${formId}')"><#if icon != ''><i class="${icon} icon"></i></#if> ${label}</button>
</#macro>

<#macro group number='' class='' inline=false>
    <div class="${number} ${class} ${inline?then('inline','')} fields">
        <#nested />
    </div>
</#macro>

<#macro field name label='' size='fluid' disabled=false required=false computed=false help='' class='' suffix=''>
    <#assign errorfield=name?replace('.id','') />
    <div id="${name?replace('.','_')}" class="${size} ${required?then('required','')} ${computed?then('computed','')} ${disabled?then('disabled','')} field ${(Form?? && Form.hasViolation(errorfield))?then('error','')} ${class}">
        <#if label?length != 0><label>${label} <#if help != ''><i class="green help circle icon popup" data-content="${help}"></i></#if></label></#if>
        <#nested />
    </div>
    <#if suffix != ''><span style="padding-top: ${(label != '')?then(22,4)}px;">${suffix}</span></#if>
</#macro>

<#macro text name label='' value=(FormData[name])!'' size='fluid' class='' disabled=false autocomplete=false readOnly=false required=false computed=false leftIcon='' rightIcon='' help='' length='' max='' labeled='' placeholder='' type='text' suffix=''>
	<@field name label size disabled required computed help class suffix>
	    <#if leftIcon != ''>
            <div class="ui left icon input">
               <i class="${leftIcon} icon"></i>
        <#elseif rightIcon != ''>
            <div class="ui right icon input">
               <i class="${rightIcon} icon"></i>
	    <#elseif labeled != ''>
	       <div class="ui labeled input">
	           <div class="ui label">${labeled}</div>
		</#if>
	    <input type="${type}" name="${name}" value="${value}" placeholder="${placeholder}" autocomplete="${autocomplete?then('on','off')}" ${required?then('required','')} ${disabled?then('disabled','')} ${readOnly?then('readOnly','')} ${(length == '')?then('','size='+length+'')} ${(max == '')?then('','maxlength='+max+'')} />
	    <#if leftIcon != '' || rightIcon != '' || labeled != ''>
	    	</div>
	    </#if>
	</@field>
</#macro>

<#macro textarea name label='' value=(FormData[name])!'' size='fluid' class='' rows='3' disabled=false required=false computed=false help=''>
    <@field name label size disabled required computed help class>
        <textarea name="${name}" rows="${rows}">${value}</textarea>
    </@field>
</#macro>

<#macro calendar name label='' value=(FormData[name])!'' size='fluid' class='' disabled=false required=false computed=false icon=true help='' defaultConfig=true>
    <#assign id=name?replace('.','_') />
    <@field name label size disabled required computed help class>
        <div id="${id}_calendar" class="ui calendar">
            <#if icon><div class="ui input left icon"><i class="grey calendar icon"></i></#if>
               <input type="text" name="${name}" value="${(value?is_date)?then(value?date?string['dd/MM/yyyy'],value)}" ${required?then('required','')} ${disabled?then('disabled','')} autocomplete="off" />
            <#if icon></div></#if>
        </div>
    </@field>
    <#if defaultConfig>
    <script type="text/javascript">
    $(document).ready(function() {
       $('#${id}_calendar').calendar({
          type : 'date', 
          firstDayOfWeek : 1, 
          today : true,
          monthFirst : false, 
          // text : calendarFr, 
          formatter : {
            date : function(date, settings) { return formatCalendarDate(date, '${lang}'); },
          },
          popupOptions: {
             observeChanges: false
          }
       });
    });
    </script>
    </#if>
</#macro>

<#macro time name label='' value=(FormData[name])!'' size='fluid' class='' disabled=false required=false computed=false icon=true help='' defaultConfig=true>
    <#assign id=name?replace('.','_') />
    <@field name label size disabled required computed help class>
        <div id="${id}_time" class="ui calendar">
            <#if icon><div class="ui input left icon"><i class="grey time icon"></i></#if>
               <input type="text" name="${name}" value="${value}" ${disabled?then('disabled','')} autocomplete="off" size="5" maxlength="5" />
            <#if icon></div></#if>
        </div>
    </@field>
    <#if defaultConfig>
    <script type="text/javascript">
    $(document).ready(function() {
       $('#${id}_time').calendar({
          type : 'time', ampm : false, // text : calendarFr
       });
    });
    </script>
    </#if>
</#macro>

<#macro select name values label='' idKey='id' labelKey='title' filter='' value=(FormData[name])!'' size='fluid' class='' disabled=false required=false computed=false multiple=false help='' sortLabel=labelKey data1='' data2='' data3=''  placeholder='Select...'>
	<@field name label size disabled required computed help class>
		 <#assign is_hash=(values?size gt 0 && values[0]?is_hash && (values[0][labelKey])??) />
		 <select name="${name}" class="ui fluid search dropdown ${name} ${required?then('required','clearable notRequired')}" ${multiple?then('multiple','')}>
		    <#if values?size == 1 && required>
		    <option <@data Value data1 data2 data3 /> value="${is_hash?then(values[0][idKey],values[0])}" selected>${is_hash?then(values[0][labelKey],values[0])}</option>
		    <#elseif values?size gt 0>
		    <option value="">${placeholder}</option>
		    <#list is_hash?then(values?sort_by(sortLabel),values) as Value>
		        <#if !is_hash>
		          <option value="${Value}">${Value}</option>
		        <#elseif filter == '' || Value[filter]>
				    <#if value?is_sequence>
				    <option <@data Value data1 data2 data3 /> value="${Value[idKey]}" ${(value?seq_contains(Value) || (value?seq_contains(Value.id)) || (required && values?size == 1))?then('selected','')}>${Value[labelKey]}</option>
				    <#else>
				    <option <@data Value data1 data2 data3 /> value="${Value[idKey]}" ${((Value[idKey] == value) || (value?is_hash && value.id?? && value.id == Value.id) || (required && values?size == 1))?then('selected','')}>${Value[labelKey]}</option>
				    </#if>
				</#if>
		    </#list>
		    </#if>
		</select>
	</@field>
</#macro>

<#macro data Value='' data1='' data2='' data3=''><#if Value != ''><#if data1 != ''>data-${data1}="${(Value[data1]?string)!''}" </#if><#if data2 != ''>data-${data2}="${(Value[data2]?string)!''}" </#if><#if data3 != ''>data-${data3}="${(Value[data3]?string)!''}" </#if></#if></#macro>

<#macro remoteSelect name url label='' filter='' value=(FormData[name])!'' size='fluid' class='' disabled=false required=false computed=false multiple=false help=''>
    <#assign id=name?replace('.','_') />
    <@field name label size disabled required computed help class>
        <div id="${id}" class="ui search selection dropdown">
            <input type="hidden" name="${name}" value="${value}" class="noselection" autocomplete="off" ${required?then('required','')} ${disabled?then('disabled','')} />
            
            <input type="text" class="search" tabindex="0">
            <div class="default text">Select one...</div>
            <div class="menu transition hidden" tabindex="-1">
                <div class="message">No results found.</div>
            </div>
        </div>
    </@field>
    <script type="text/javascript">
    $(document).ready(function() {
        $('#${id}').dropdown({
            saveRemoteData: false,
            minCharacters: 3,
            fullTextSearch: true,
            match: 'text',
            clearable: true,
            apiSettings: {
                url: '${url}{query}',
            },
        });
    });
    </script>
</#macro>

<#macro remoteSearch name url label='' value=(FormData[name])!'' size='fluid' disabled=false required=false apiName=true>
    <#assign id='search_'+name?replace('.','_') />
    <@f.field name label size disabled required>
        <div id="${id}" class="ui left aligned category search item">
           <input class="prompt" type="text" name="${name}" value="${value}" autocomplete="off" ${required?then('required','')} ${disabled?then('disabled','')} maxlenght="60" />
           <div class="tiny results"></div>
        </div>
    </@f.field>
    <script type="text/javascript">
    $(document).ready(function() {
        $('#${id}').search({
           minCharacters: 3,
           maxResults: 10,
           showNoResults: false,
           apiSettings: {
              url: '${url}{query}',
           },
        });
    });
    </script>
</#macro>

<#macro enum name values prefix='' labelKey='text' label='' value=(FormData[name])!'' size='fluid' class='' disabled=false required=false computed=false multiple=false help='' skip=-1 skip2=-1 param1='' param2='' placeholder='Select...'>
    <@field name label size disabled required computed help class>
        <select name="${name}" class="ui fluid ${multiple?then('multiple',required?then('select required','select clearable notRequired'))} dropdown ${name?replace('.','_')}" ${required?then('required','')} ${multiple?then('multiple','')} ${disabled?then('disabled','')}>
            <option value="">${placeholder}</option>
            <#list values as Value>
            <#if Value?index != skip && Value?index != skip2 && (prefix == '' || i18n(prefix+'.'+Value) != '')>
            	<option value="${Value}" ${((value?is_sequence && value?seq_contains(Value) || (!value?is_sequence && value == Value)))?then('selected','')}>${(prefix != '')?then(i18n(prefix+'.'+Value,param1,param2), (labelKey != '')?then(Value[labelKey],Value))}</option>
           	</#if>
            </#list>
        </select>
    </@field>
</#macro>

<#-- type can be toggle or slider -->
<#macro boolean name label='' value=(FormData[name])!false size='fluid' class='' disabled=false required=false computed=false topLabel='' help='' type=''>
    <@checkbox name 'true' label value size class disabled required computed topLabel help type />
</#macro>

<#-- type can be toggle or slider -->
<#macro checkbox name value=(FormData[name])!'' label='' selected=false size='fluid' class='' disabled=false required=false computed=false topLabel='' help='' type=''>
    <@field name topLabel size disabled required computed help class>
    <div id="${name?replace('.','_')}" class="boolean ${required?then('required','')} ${disabled?then('disabled','')} inline field ${(Form?? && Form.hasViolation(errorfield))?then('error','')}">
        <div class="ui ${type} checkbox">
            <input type="checkbox" name="${name}" value="${value}" tabindex="0" ${((selected?is_boolean && selected) || (selected?is_string && selected == 'true'))?then('checked','')}>
            <label>${label}</label>
        </div>
    </div>
    </@field>
</#macro>
