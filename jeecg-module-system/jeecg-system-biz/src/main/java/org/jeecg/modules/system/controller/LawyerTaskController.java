package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.LawyerTask;
import org.jeecg.modules.system.entity.LawyerTaskInfo;
import org.jeecg.modules.system.mapper.SysUserRoleMapper;
import org.jeecg.modules.system.service.ILawyerTaskInfoService;
import org.jeecg.modules.system.service.ILawyerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: lawyer_task
 * @Author: jeecg-boot
 * @Date: 2024-03-02
 * @Version: V1.0
 */
@RestController
@RequestMapping("/lawyer/lawyerTask")
@Slf4j
public class LawyerTaskController extends JeecgController<LawyerTask, ILawyerTaskService> {
    @Autowired
    private ILawyerTaskService lawyerTaskService;
    @Autowired
    private ILawyerTaskInfoService taskInfoService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 分页列表查询
     *
     * @param lawyerTask
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "lawyer_task-分页列表查询")
    @ApiOperation(value = "lawyer_task-分页列表查询", notes = "lawyer_task-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<LawyerTask>> queryPageList(LawyerTask lawyerTask,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> roleNameList = sysUserRoleMapper.getRoleNameByUserName(loginUser.getUsername());
        log.info("当前登陆人角色为{}",JSONObject.toJSONString(roleNameList));
        String roleName = roleNameList.get(0);
        if(!roleName.equals("超级管理员") && !roleName.equals("管理员")){
            lawyerTask.setCreateUser(loginUser.getUsername());
        }
        QueryWrapper<LawyerTask> queryWrapper = QueryGenerator.initQueryWrapper(lawyerTask, req.getParameterMap());
        Page<LawyerTask> page = new Page<LawyerTask>(pageNo, pageSize);
        IPage<LawyerTask> pageList = lawyerTaskService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param lawyerTask
     * @return
     */
    @AutoLog(value = "lawyer_task-添加")
    @ApiOperation(value = "lawyer_task-添加", notes = "lawyer_task-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody LawyerTask lawyerTask) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        lawyerTask.setCreateUser(loginUser.getUsername());
        List<String> roleNameList = sysUserRoleMapper.getRoleNameByUserName(loginUser.getUsername());
        log.info("当前登陆人角色为{}",JSONObject.toJSONString(roleNameList));
        String roleName = roleNameList.get(0);
        LambdaQueryWrapper<LawyerTask> queryWrapper =  new LambdaQueryWrapper();
        queryWrapper.eq(LawyerTask::getStatus,0);
        queryWrapper.eq(LawyerTask::getCreateUser,loginUser.getUsername());
        Long num = lawyerTaskService.count(queryWrapper);
        if(roleName.equals("基础版" )&& num.intValue()>= 1){
            return Result.error("当前存在进行中任务！");
        }else if(roleName.equals("标准版") && num.intValue()>= 2){
            return Result.error("当前进行中任务,超过账号上限！");
        }if(roleName.equals("升级版") && num.intValue()>= 3){
            return Result.error("当前进行中任务,超过账号上限！");
        }
        lawyerTaskService.save(lawyerTask);
        String id = lawyerTask.getId();
        log.info("lawyer_task 保存后Id为{}", JSONObject.toJSONString(lawyerTask));
        redisTemplate.opsForValue().set("lawyer_task:" + id, "1");
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param lawyerTask
     * @return
     */
    @AutoLog(value = "lawyer_task-编辑")
    @ApiOperation(value = "lawyer_task-编辑", notes = "lawyer_task-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody LawyerTask lawyerTask) {
        lawyerTaskService.updateById(lawyerTask);
        if (lawyerTask.getStatus() != null && lawyerTask.getStatus() == 2) {
            redisTemplate.opsForValue().set("lawyer_task:" + lawyerTask.getId(), "2");
        }
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "lawyer_task-通过id删除")
    @ApiOperation(value = "lawyer_task-通过id删除", notes = "lawyer_task-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        lawyerTaskService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "lawyer_task-批量删除")
    @ApiOperation(value = "lawyer_task-批量删除", notes = "lawyer_task-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.lawyerTaskService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "lawyer_task-通过id查询")
    @ApiOperation(value = "lawyer_task-通过id查询", notes = "lawyer_task-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<LawyerTask> queryById(@RequestParam(name = "id", required = true) String id,
                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        LawyerTask lawyerTask = lawyerTaskService.getById(id);
        if (lawyerTask == null) {
            return Result.error("未找到对应数据");
        }
        if (lawyerTask.getStatus() == 1) {
            QueryWrapper<LawyerTaskInfo> queryWrapper = new QueryWrapper<>();
            Page<LawyerTaskInfo> page = new Page<LawyerTaskInfo>(pageNo, pageSize);
            IPage<LawyerTaskInfo> pageList = taskInfoService.page(page, queryWrapper);
            lawyerTask.setPageInfoList(pageList);
        }
        return Result.OK(lawyerTask);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param lawyerTask
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, LawyerTask lawyerTask) {
        return super.exportXls(request, lawyerTask, LawyerTask.class, "lawyer_task");
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
        return super.importExcel(request, response, LawyerTask.class);
    }

}
