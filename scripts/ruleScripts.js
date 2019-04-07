function capitalize(str) {
    str = str.toString()
    return str[0].toUpperCase() + str.substring(1)
}

function underlineToCamel(str) {
    str = str.toString()
    holder = []
    for(var i=0; i<str.length; i++) {
        if(str[i] === '_') {
            holder.push(str[++i].toUpperCase());
        } else {
            holder.push(str[i])
        }
    }
    return holder.join('')
}

var viewNameMap = {
    't_userGroupVo': 'select u.user_id, u.name as user_name, u.desc as user_desc, g.name as group_name from my_sample_db.t_user u inner join my_sample_db.t_group g using(group_id) limit 0,1'
}

function moduleNameRule(tableName) {
    var temp = tableName.toString().replace(/^t_/, "");
    return capitalize(temp)
}

function propertyNameRule(columnName) {
    return underlineToCamel(columnName)
}

/**
 * 是否转换当前模型的过滤器
 * @param model {@link me.liqiu.mybatisgeneratetools.TableModel}
 * @returns {boolean}
 */
function filterRule(model) {
    // return true
    return /^t_/.test(model.tableName.toString())
}

