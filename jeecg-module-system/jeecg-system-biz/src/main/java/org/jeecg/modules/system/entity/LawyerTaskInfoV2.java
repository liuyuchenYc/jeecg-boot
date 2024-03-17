package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description: lawyer_task_info
 * @Author: jeecg-boot
 * @Date:   2024-03-02
 * @Version: V1.0
 */
@Data
@TableName("lawyer_task_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="lawyer_task_info对象", description="lawyer_task_info")
public class LawyerTaskInfoV2<T> implements Serializable {
    private static final long serialVersionUID = 1L;

	/**线索id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "线索id")
    private String id;


	/**渠道*/
	@Excel(name = "渠道", width = 15)
    @ApiModelProperty(value = "渠道")
    private String channel;
	/**内容*/
	@Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
    private String content;
	/**内容id*/
	@Excel(name = "内容id", width = 15)
    @ApiModelProperty(value = "内容id")
    private String contentId;
	/**内容作者*/
	@Excel(name = "内容作者", width = 15)
    @ApiModelProperty(value = "内容作者")
    private String contentAuthor;
	/**内容作者id*/
	@Excel(name = "内容作者id", width = 15)
    @ApiModelProperty(value = "内容作者id")
    private String contentAuthorId;
	/**地区*/
	@Excel(name = "地区", width = 15)
    @ApiModelProperty(value = "地区")
    private String area;
	/**内容地址*/
	@Excel(name = "内容地址", width = 15)
    @ApiModelProperty(value = "内容地址")
    private String contentUrl;
	/**用户签名*/
	@Excel(name = "用户签名", width = 15)
    @ApiModelProperty(value = "用户签名")
    private String userSign;

    @TableField(exist = false)
    private String searchDomain;
    @Excel(name = "销售额", width = 15)
    private BigDecimal totalSale;
    @Excel(name = "是否标记", width = 15 ,replace={"是_1","否_0"})
    private Integer marks;
}
