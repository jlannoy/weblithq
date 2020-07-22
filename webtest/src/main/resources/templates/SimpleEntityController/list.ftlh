<@page.app title='SimpleEntity list' selectedIcon='list'>
    <@layout.title />
    <#list SimpleEntities>
        <ul>
            <#items as SimpleEntity>
                <li>
                    ${SimpleEntity.name} 
                    | <a href="${contextPath}/SimpleEntity/view/${SimpleEntity.id}">view by id</a>
                    | <a href="${contextPath}/SimpleEntity/name/${SimpleEntity.name?url}">view by name</a>
                    | <a href="${contextPath}/SimpleEntity/edit/${SimpleEntity.id?url}">edit</a>
                </li>
            </#items>
        </ul>
    </#list>
    <p>Total : ${SimpleEntities?size} x SimpleEntity</p>
    <p><a href="${contextPath}/SimpleEntity/create">Create</a></p>
    <p><a href="${contextPath}/SimpleEntity/createMultiple">Create multiple</a></p>
</@page.app>