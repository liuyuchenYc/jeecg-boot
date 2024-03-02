package org.jeecg.modules.lawyer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: lawyer_task
 * @Author: jeecg-boot
 * @Date:   2024-03-02
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
    private Integer id;
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private String taskName;
	/**关键词*/
	@Excel(name = "关键词", width = 15)
    @ApiModelProperty(value = "关键词")
    private String keyWord;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**完成时间*/
	@Excel(name = "完成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "完成时间")
    private Date finishTime;
	/**任务状态0进行中1完成2停止*/
	@Excel(name = "任务状态0进行中1完成2停止", width = 15)
    @ApiModelProperty(value = "任务状态0进行中1完成2停止")
    private Integer status;
	/**线索条数*/
	@Excel(name = "线索条数", width = 15)
    @ApiModelProperty(value = "线索条数")
    private Integer clueTotal;
	/**是否有效0否1是*/
	@Excel(name = "是否有效0否1是", width = 15)
    @ApiModelProperty(value = "是否有效0否1是")
    private Integer yn;
	/**检索域*/
	@Excel(name = "检索域", width = 15)
    @ApiModelProperty(value = "检索域")
    private String searchDomain;
}
