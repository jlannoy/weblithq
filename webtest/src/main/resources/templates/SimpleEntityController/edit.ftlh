<@page.app title=SimpleEntity.id???then('Edit','Create')+' SimpleEntity' selectedIcon='list'>
    
    <@f.form 'SimpleEntity' SimpleEntity.id>
        <@f.group 'three'>
            <@f.text name='name' label='Name' size='eight wide' required=true />
            <@f.text name='quantity' label='Quantity (min. 1)' type='number' required=true />
            <@f.text name='quantity2' label='Quantity 2 (not typed)' />
        </@>
        <@f.group 'two'>
            <@f.enum name='type' label='Type' values=Types prefix='enum.Type' required=true />
            <@f.calendar name='date' label='Date' required=true />
        </@>
        <@f.buttonBar>
            <@f.cancel />
            <@f.submit />
        </@>
    </@>

</@>