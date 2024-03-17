package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.LawyerTask;
import org.jeecg.modules.system.entity.LawyerTaskChannel;
import org.jeecg.modules.system.entity.LawyerTaskInfo;
import org.jeecg.modules.system.mapper.LawyerTaskChannelMapper;
import org.jeecg.modules.system.mapper.LawyerTaskMapper;
import org.jeecg.modules.system.service.ILawyerTaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

 /**
 * @Description: lawyer_task_info
 * @Author: jeecg-boot
 * @Date:   2024-03-02
 * @Version: V1.0
 */
@Api(tags="lawyer_task_info")
@RestController
@RequestMapping("/lawyer/lawyerTaskInfo")
@Slf4j
public class LawyerTaskInfoController extends JeecgController<LawyerTaskInfo, ILawyerTaskInfoService> {
	@Autowired
	private ILawyerTaskInfoService lawyerTaskInfoService;

	@Resource
	private LawyerTaskChannelMapper taskChannelMapper;

	@Autowired
	private LawyerTaskMapper lawyerTaskMapper;
	/**
	 * 分页列表查询
	 *
	 * @param lawyerTaskInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "lawyer_task_info-分页列表查询")
	@ApiOperation(value="lawyer_task_info-分页列表查询", notes="lawyer_task_info-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<LawyerTaskInfo>> queryPageList(LawyerTaskInfo lawyerTaskInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {

		LambdaQueryWrapper<LawyerTaskInfo> infoQueryWrapper = new LambdaQueryWrapper<>();
		infoQueryWrapper.eq(LawyerTaskInfo::getTaskId,lawyerTaskInfo.getTaskId());
		List<String> ids = new ArrayList<>();
		if(!StringUtils.isEmpty(lawyerTaskInfo.getChannel())){
			LambdaQueryWrapper<LawyerTaskChannel> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(LawyerTaskChannel::getChannelType,lawyerTaskInfo.getSearchDomain());
			queryWrapper.eq(LawyerTaskChannel::getYn,1);
			List<LawyerTaskChannel> taskChannelList = taskChannelMapper.selectList(queryWrapper);
			Map<String,Object> map = taskChannelList.stream().collect(Collectors.toMap(LawyerTaskChannel::getServiceNum,p->p.getChannel()));
			String channel = lawyerTaskInfo.getChannel();
			String [] channelArray = channel.split(",");
			Arrays.stream(channelArray).forEach(item->{
					ids.add(map.get(item).toString());
			});
			infoQueryWrapper.in(LawyerTaskInfo::getChannel,ids);
		}
		if(!StringUtils.isEmpty(lawyerTaskInfo.getProductSummary())){
			infoQueryWrapper.like(LawyerTaskInfo::getProductSummary,lawyerTaskInfo.getProductSummary());
		}
		if(lawyerTaskInfo.getMarks()!=null) {
			infoQueryWrapper.eq(LawyerTaskInfo::getMarks, lawyerTaskInfo.getMarks());
		}
		if(!StringUtils.isEmpty(lawyerTaskInfo.getProductTitle())) {
			infoQueryWrapper.like(LawyerTaskInfo::getProductTitle, lawyerTaskInfo.getProductTitle());
		}
		Page<LawyerTaskInfo> page = new Page<LawyerTaskInfo>(pageNo, pageSize);
		log.info("req{}", JSONObject.toJSONString(infoQueryWrapper.getSqlSelect()));
		IPage<LawyerTaskInfo> pageList = lawyerTaskInfoService.page(page,infoQueryWrapper );
		List<LawyerTaskInfo> pageRecord = pageList.getRecords();
		String searchDomain = "";
		if(!CollectionUtils.isEmpty(pageRecord)){
			LawyerTaskInfo info = pageRecord.get(0);
			String taskId = info.getTaskId();
			LambdaQueryWrapper<LawyerTask> taskLambdaQueryWrapper = new LambdaQueryWrapper<>();
			taskLambdaQueryWrapper.eq(LawyerTask::getId,taskId);
			LawyerTask lawyerTask = lawyerTaskMapper.selectOne(taskLambdaQueryWrapper);
			searchDomain = lawyerTask.getSearchDomain();
		}
		if(!StringUtils.isEmpty(searchDomain) && searchDomain.equals("2")){
			List<LawyerTaskInfo> list = pageList.getRecords();
			list.stream().forEach(item->{
				if(item.getSalesVolume() == null){
					BigDecimal productPrice = item.getCommodityPrice() == null ? BigDecimal.ZERO :item.getCommodityPrice();
					BigDecimal saleVolume = item.getSalesVolume() == null ? BigDecimal.ZERO :new BigDecimal(item.getSalesVolume());
					BigDecimal totalPrice = productPrice.multiply(saleVolume);
					item.setTotalSale(totalPrice);
				}else if(item.getCommodityPrice() != null && item.getSalesVolume() != null){
					BigDecimal productPrice = item.getCommodityPrice() == null ? BigDecimal.ZERO :item.getCommodityPrice();
					BigDecimal saleVolume = item.getSalesVolume() == null ? BigDecimal.ZERO :new BigDecimal(item.getSalesVolume());
					BigDecimal totalPrice = productPrice.multiply(saleVolume);
					item.setTotalSale(totalPrice);
				}else{
					item.setTotalSale(BigDecimal.ZERO);
				}
			});
			pageList.setRecords(list);
		}
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param lawyerTaskInfo
	 * @return
	 */
	@AutoLog(value = "lawyer_task_info-添加")
	@ApiOperation(value="lawyer_task_info-添加", notes="lawyer_task_info-添加")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody LawyerTaskInfo lawyerTaskInfo) {
		lawyerTaskInfoService.save(lawyerTaskInfo);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param lawyerTaskInfo
	 * @return
	 */
	@AutoLog(value = "lawyer_task_info-编辑")
	@ApiOperation(value="lawyer_task_info-编辑", notes="lawyer_task_info-编辑")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody LawyerTaskInfo lawyerTaskInfo) {
		lawyerTaskInfoService.updateById(lawyerTaskInfo);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "lawyer_task_info-通过id删除")
	@ApiOperation(value="lawyer_task_info-通过id删除", notes="lawyer_task_info-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		lawyerTaskInfoService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "lawyer_task_info-批量删除")
	@ApiOperation(value="lawyer_task_info-批量删除", notes="lawyer_task_info-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.lawyerTaskInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "lawyer_task_info-通过id查询")
	@ApiOperation(value="lawyer_task_info-通过id查询", notes="lawyer_task_info-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<LawyerTaskInfo> queryById(@RequestParam(name="id",required=true) String id) {
		LawyerTaskInfo lawyerTaskInfo = lawyerTaskInfoService.getById(id);
		if(lawyerTaskInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(lawyerTaskInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param lawyerTaskInfo
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LawyerTaskInfo lawyerTaskInfo) {
		LambdaQueryWrapper<LawyerTaskInfo> infoQueryWrapper = new LambdaQueryWrapper<>();
//		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		infoQueryWrapper.eq(LawyerTaskInfo::getTaskId,lawyerTaskInfo.getTaskId());
		List<String> ids = new ArrayList<>();
		if(lawyerTaskInfo.getChannel() != null){
			LambdaQueryWrapper<LawyerTaskChannel> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(LawyerTaskChannel::getChannelType,lawyerTaskInfo.getSearchDomain());
			queryWrapper.eq(LawyerTaskChannel::getYn,1);
			List<LawyerTaskChannel> taskChannelList = taskChannelMapper.selectList(queryWrapper);
			Map<String,Object> map = taskChannelList.stream().collect(Collectors.toMap(LawyerTaskChannel::getServiceNum,p->p.getChannel()));
			String channel = lawyerTaskInfo.getChannel();
			String [] channelArray = channel.split(",");
			Arrays.stream(channelArray).forEach(item->{
				ids.add(map.get(item).toString());
			});
			infoQueryWrapper.in(LawyerTaskInfo::getChannel,ids);
		}
		if(!StringUtils.isEmpty(lawyerTaskInfo.getProductSummary())){
			infoQueryWrapper.like(LawyerTaskInfo::getProductSummary,lawyerTaskInfo.getProductSummary());
		}
		if(!StringUtils.isEmpty(lawyerTaskInfo.getProductTitle())) {
			infoQueryWrapper.like(LawyerTaskInfo::getProductTitle, lawyerTaskInfo.getProductTitle());
		}
		if(lawyerTaskInfo.getMarks()!=null) {
			infoQueryWrapper.eq(LawyerTaskInfo::getMarks, lawyerTaskInfo.getMarks());
		}
		if(lawyerTaskInfo.getSearchDomain().equals("2")){
			return super.newExportXls(request,  LawyerTaskInfo.class, "线索详情报表",infoQueryWrapper,1);
		}else{
			return super.newExportXls(request,  LawyerTaskInfo.class, "线索详情报表",infoQueryWrapper,2);
		}

    }
    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, LawyerTaskInfo.class);
    }

}
