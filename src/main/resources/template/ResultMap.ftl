<resultMap id="${objectName}" type="<#if objectFullName?starts_with('.')>${objectFullName[1..]}<#else>${objectFullName}</#if>">
<#list tableColumnNames as column>
<#assign columnName = column>
<#assign propertyName = objectPropertyNames[column_index]>
<#if column_index == 0 >
    <id property="${propertyName}" column="${columnName}"/>
<#else>
    <result property="${propertyName}" column="${columnName}"/>
</#if>
</#list>
</resultMap>
