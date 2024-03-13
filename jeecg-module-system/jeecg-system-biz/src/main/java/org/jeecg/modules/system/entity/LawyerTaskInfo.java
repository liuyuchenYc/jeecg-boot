package org.jeecg.modules.system.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class LawyerTaskInfo extends LawyerTaskInfoV2 implements Serializable {
    private static final long serialVersionUID = 1L;

	/**线索id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "线索id")
    private java.lang.String id;
	/**商品标题*/
	@Excel(name = "商品标题", width = 15)
    @ApiModelProperty(value = "商品标题")
    private java.lang.String productTitle;
	/**商品描述*/
	@Excel(name = "商品描述", width = 15)
    @ApiModelProperty(value = "商品描述")
    private java.lang.String productSummary;
	/**商品封面*/
	@Excel(name = "商品封面", width = 15 ,type = 2)
    @ApiModelProperty(value = "商品封面")
    private java.lang.String productCover;
	/**主体类型*/
	@Excel(name = "主体类型", width = 15)
    @ApiModelProperty(value = "主体类型")
    private java.lang.String subjectType;
	/**主体信息*/
	@Excel(name = "主体信息", width = 15)
    @ApiModelProperty(value = "主体信息")
    private java.lang.String subjectInfo;
	/**店铺名称*/
	@Excel(name = "店铺名称", width = 15)
    @ApiModelProperty(value = "店铺名称")
    private java.lang.String shopName;
	/**商品价格*/
	@Excel(name = "商品价格", width = 15)
    @ApiModelProperty(value = "商品价格")
    private java.math.BigDecimal commodityPrice;
	/**商品销量*/
	@Excel(name = "商品销量", width = 15)
    @ApiModelProperty(value = "商品销量")
    private java.lang.Integer salesVolume;
	/**任务id*/
	@Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private java.lang.String taskId;
	/**渠道*/
	@Excel(name = "渠道", width = 15)
    @ApiModelProperty(value = "渠道")
    private java.lang.String channel;
	/**内容*/
	@Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
    private java.lang.String content;
	/**内容id*/
	@Excel(name = "内容id", width = 15)
    @ApiModelProperty(value = "内容id")
    private java.lang.String contentId;
	/**内容作者*/
	@Excel(name = "内容作者", width = 15)
    @ApiModelProperty(value = "内容作者")
    private java.lang.String contentAuthor;
	/**内容作者id*/
	@Excel(name = "内容作者id", width = 15)
    @ApiModelProperty(value = "内容作者id")
    private java.lang.String contentAuthorId;
	/**地区*/
	@Excel(name = "地区", width = 15)
    @ApiModelProperty(value = "地区")
    private java.lang.String area;
	/**内容地址*/
	@Excel(name = "内容地址", width = 15)
    @ApiModelProperty(value = "内容地址")
    private java.lang.String contentUrl;
	/**商品链接*/
	@Excel(name = "商品链接", width = 15)
    @ApiModelProperty(value = "商品链接")
    private java.lang.String productLink;
	/**用户签名*/
	@Excel(name = "用户签名", width = 15)
    @ApiModelProperty(value = "用户签名")
    private java.lang.String userSign;

    @TableField(exist = false)
    private String searchDomain;
    @Excel(name = "销售额", width = 15)
    private BigDecimal totalSale;
    @Excel(name = "是否标记", width = 15,replace={"是_1","否_0"})
    private Integer marks;
}
