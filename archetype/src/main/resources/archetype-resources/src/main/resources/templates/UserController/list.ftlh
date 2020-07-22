<@page.app title='User list' selectedIcon='users'>
    <@layout.title />
    <@t.table objectName='User' rows=Users align='left' noButtons=true>
        <@t.column name='role' title='Role' align='center' />
        <@t.column name='title' title='Title' />
        <@t.column name='email' title='E-mail' interpret='<@layout.mailto value />' />
    </@t.table>
    <@layout.divider />
    <p>Session test : last time you visited this page was <i>${lastVisit!'Never'}</i></p>
</@page.app>