<@page.app title='Create Multiple SimpleEntity' selectedIcon='list'>
    
    <@f.form action='/SimpleEntity/saveMultiple'>
        <div class="ui two columns grid">
            <div class="row">
                <div class="column">
                    <@myForm "entities" />
                </div>
                <div class="column">
                    <@myForm "entities" />
                </div>
            </div>
            <div class="row">
                <@f.buttonBar>
                    <@f.cancel />
                    <@f.submit />
                </@>
            </div>
        </div>
    </@>
</@>

<#macro myForm prefix=''>
    <@f.text name=prefix+'.name' label='Name' />
    <@f.text name=prefix+'.type' label='Type' />
    <@f.calendar name=prefix+'.date' label='Date' />
</#macro>