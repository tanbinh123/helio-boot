package cc.uncarbon.module.sys.model.response;

import cc.uncarbon.framework.core.context.TenantContext;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;


/**
 * 登录后返回的字段
 * 用于内部 RPC 调用
 * @author Uncarbon
 */
@Accessors(chain = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SysUserLoginBO implements Serializable {

    @ApiModelProperty(value = "用户ID")
    private Long id;

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号")
    private String phoneNo;

    @ApiModelProperty(value = "对应角色ID")
    private Collection<Long> roleIds;

    @ApiModelProperty(value = "对应角色")
    private Collection<String> roles;

    @ApiModelProperty(value = "拥有权限")
    private Collection<String> permissions;

    @ApiModelProperty(value = "关联租户上下文")
    private TenantContext tenantContext;

}
