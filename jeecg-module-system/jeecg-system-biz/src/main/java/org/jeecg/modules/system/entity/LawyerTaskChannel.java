package org.jeecg.modules.system.entity;

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
@TableName("lawyer_task_channel")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="lawyer_task_channel", description="lawyer_task_channel")
public class LawyerTaskChannel implements Serializable {
    private static final long serialVersionUID = 1L;

	/**任务id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "服务id")
    private Integer id;
	/**任务名称*/
	@Excel(name = "渠道", width = 15)
    @ApiModelProperty(value = "渠道")
    private String channel;
	/**关键词*/
	@Excel(name = "服务名称", width = 15)
    @ApiModelProperty(value = "关键词")
    private String serviceName;
	@Excel(name = "渠道类型 1内容 2商品", width = 15)
    @ApiModelProperty(value = "1内容 2商品")
    private Integer channelType;
	/**是否有效0否1是*/
	@Excel(name = "是否有效0否1是", width = 15)
    @ApiModelProperty(value = "是否有效0否1是")
    private Integer yn;

    @Excel(name = "是否有效0否1是", width = 15)
    @ApiModelProperty(value = "是否有效0否1是")
	private String serviceNum;
}
