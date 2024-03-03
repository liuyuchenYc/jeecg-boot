package org.jeecg.modules.quartz.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 更新过期用户
 * @Author Scott
 */
@Slf4j
public class LawyerUserJob implements Job {

	@Autowired
	private ISysUserService iSysUserService;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		log.info("更新过期用户");
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String formattedDate = currentDate.format(formatter);

		LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(SysUser::getExpirationDate,formattedDate);
		lambdaQueryWrapper.eq(SysUser::getStatus,1);
		List<SysUser> expireList = iSysUserService.list(lambdaQueryWrapper);
		//过期集合
		expireList.stream().forEach(item->{
			log.info("用户{}账号效期已过期,冻结",item.getUsername());
			iSysUserService.updateStatus(item.getId(),"2");
		});
	}
}
