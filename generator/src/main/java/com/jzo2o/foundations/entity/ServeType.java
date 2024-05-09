package com.jzo2o.foundations.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 服务类型表
 * </p>
 *
 * @author author
 * @since 2024-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("serve_type")
@ApiModel(value="ServeType对象", description="服务类型表")
public class ServeType implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "服务类型id")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "服务类型编码")
    @TableField("code")
    private String code;

    @ApiModelProperty(value = "服务类型名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "服务类型图标")
    @TableField("serve_type_icon")
    private String serveTypeIcon;

    @ApiModelProperty(value = "服务类型图片")
    @TableField("img")
    private String img;

    @ApiModelProperty(value = "排序字段")
    @TableField("sort_num")
    private Integer sortNum;

    @ApiModelProperty(value = "是否启用，0草稿,1禁用，2启用")
    @TableField("active_status")
    private Integer activeStatus;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "创建者")
    @TableField("create_by")
    private Long createBy;

    @ApiModelProperty(value = "更新者")
    @TableField("update_by")
    private Long updateBy;


}
