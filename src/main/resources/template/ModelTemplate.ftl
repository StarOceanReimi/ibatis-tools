<#if packageName??>
package ${packageName};
</#if>
import lombok.Data;
<#list importClasses as import>
import ${import};
</#list>

/**
 * 表${tableName}的模型
 * @description 工具自动生成
 */
@Data
public class ${className} {

<#list classFields as field>
    /**
     * 对应数据库字段 -> ${tableColumnNames[field_index]}
     */
    private ${field.type} ${field.name};

</#list>
}