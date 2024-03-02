package org.jeecg.modules.task.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: lawyer_task
 * @Author: jeecg-boot
 * @Date:   2024-02-27
 * @Version: V1.0
 */
@Data
@TableName("lawyer_task")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="lawyer_task对象", description="lawyer_task")
public class LawyerTask implements Serializable {
    private static final long serialVersionUID = 1L;

	/**任务id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "任务id")
    private java.lang.Long id;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String taskName;
	/**关键词*/
	@Excel(name = "关键词", width = 15)
    @ApiModelProperty(value = "关键词")
    private java.lang.String keyWord;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**完成时间*/
	@Excel(name = "完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "完成时间")
    private java.util.Date finishTime;
	/**任务状态0进行中1完成2停止*/
	@Excel(name = "任务状态0进行中1完成2停止", width = 15)
    @ApiModelProperty(value = "任务状态0进行中1完成2停止")
    private java.lang.Integer status;
	/**线索条数*/
	@Excel(name = "线索条数", width = 15)
    @ApiModelProperty(value = "线索条数")
    private java.lang.Integer clueTotal;
	/**是否有效0否1是*/
	@Excel(name = "是否有效0否1是", width = 15)
    @ApiModelProperty(value = "是否有效0否1是")
    private java.lang.Integer yn;
	/**检索域*/
	@Excel(name = "检索域", width = 15)
    @ApiModelProperty(value = "检索域")
    private java.lang.String searchDomain;
}
