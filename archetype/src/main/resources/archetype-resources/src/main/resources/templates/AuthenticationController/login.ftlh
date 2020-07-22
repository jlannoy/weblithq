<@page.board title=i18n('login') subtitle=i18n('welcome2')>
    <@f.form action='/j_security_check'>
        <p>Use one of : <i>a@a.com/admin, m@m.com/manager, u@u.com/user</i></p>
        <@layout.divider />
        <@f.text name='j_username' label='Your e-mail address' />
        <@f.text name='j_password' label='Your password' type='password' />
        <@f.buttonBar>
            ${error!''}
            <@f.submit label='Identify' icon='user' />
        </@f.buttonBar>
    </@>
</@>