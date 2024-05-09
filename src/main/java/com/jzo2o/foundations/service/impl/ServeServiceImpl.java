package com.jzo2o.foundations.service.impl;



import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.RegionMapper;
import com.jzo2o.foundations.mapper.ServeItemMapper;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import com.jzo2o.mvc.advice.CommonExceptionAdvice;
import com.jzo2o.mysql.utils.PageHelperUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ServeServiceImpl  extends ServiceImpl<ServeMapper, Serve> implements IServeService {


    @Resource
    ServeItemMapper serveItemMapper;
    @Resource
    RegionMapper regionMapper;

    /**
     * 分页查询
     * @param servePageQueryReqDTO 查询条件
     * @return 分页结果
     */
    @Override
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO){
         PageResult<ServeResDTO> serveResDTOPageResult= PageHelperUtils.selectPage(servePageQueryReqDTO,
                ()->baseMapper.queryServeListByRegionId(servePageQueryReqDTO.getRegionId()));
         return serveResDTOPageResult;
    }

    /**
     *
     * @param serveUpsertReqDTOS 批量新增数据
     */
    @Override
    public void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOS){
        for (ServeUpsertReqDTO serveUpsertReqDTO:serveUpsertReqDTOS){
            //1.校验服务项是否为启用状态，不是启用状态不能新增
            ServeItem serveItem=serveItemMapper.selectById(serveUpsertReqDTO.getServeItemId());

            //如果服务项信息不存在或未启用
            if (ObjectUtil.isNull(serveItem) || serveItem.getActiveStatus()!= FoundationStatusEnum.ENABLE.getStatus()){
                throw new ForbiddenOperationException("该服务未启用无法添加到区域下使用");
            }
            //2.校验是否重复新增
            Integer count=lambdaQuery()
                    .eq(Serve::getRegionId,serveUpsertReqDTO.getRegionId())
                    .eq(Serve::getServeItemId,serveUpsertReqDTO.getServeItemId())
                    .count();
            if (count>0){
                throw new ForbiddenOperationException(serveItem.getName()+"服务已存在");
            }

            //3.新增服务
            Serve serve= BeanUtils.toBean(serveUpsertReqDTO,Serve.class);
            Region region=regionMapper.selectById(serveUpsertReqDTO.getRegionId());
            serve.setCityCode(region.getCityCode());
            baseMapper.insert(serve);
        }
    }

    public Serve update(Long id, BigDecimal price){
        //更新服务价格
        boolean update=lambdaUpdate()
                .eq(Serve::getId,id)
                .set(Serve::getPrice,price)
                .update();

        if (!update){
            throw new CommonException("修改服务价格失败");
        }

        return baseMapper.selectById(id);
    }

    public Serve onSale(Long id){
        Serve serve=baseMapper.selectById(id);
        if (ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }
        //上架状态
        Integer saleStatus=serve.getSaleStatus();
        if (!(saleStatus==FoundationStatusEnum.INIT.getStatus() || saleStatus==FoundationStatusEnum.DISABLE.getStatus())){
            throw new ForbiddenOperationException("草稿或下架状态方可上架");
        }

        //服务项id
        Long serveItemId=serve.getServeItemId();
        ServeItem serveItem=serveItemMapper.selectById(serveItemId);
        if (ObjectUtil.isNull(serveItem)){
            throw new ForbiddenOperationException("所属服务项不存在");
        }

        //服务项的启用状态
        Integer activeStatus=serveItem.getActiveStatus();
        //服务项为启用状态方可上架
        if (!(FoundationStatusEnum.ENABLE.getStatus()==activeStatus)){
            throw new ForbiddenOperationException("服务项为启用状态方可上架");
        }

        //更新上架状态
        boolean update=lambdaUpdate()
                .eq(Serve::getId,id)
                .set(Serve::getSaleStatus,FoundationStatusEnum.ENABLE.getStatus())
                .update();
        if (!update){
            throw new ForbiddenOperationException("启动服务失败");
        }
        return baseMapper.selectById(id);
    }

    public Serve offSale(Long id){
        //判断前端传入的id的有效性
        Serve serve=baseMapper.selectById(id);
        if (ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("区域服务不存在");
        }
        //判断服务的上架状态
        Integer status=serve.getSaleStatus();
        if (!(status==FoundationStatusEnum.ENABLE.getStatus() || status==FoundationStatusEnum.INIT.getStatus())){
            throw new ForbiddenOperationException("区域服务当前未上架，不能进行下架操作");
        }
        //判断相应的服务项id是否存在
        Long serveItemId=serve.getServeItemId();
        ServeItem serveItem=serveItemMapper.selectById(serveItemId);
        if (ObjectUtil.isNull(serveItem)){
            throw new ForbiddenOperationException("该服务不存在");
        }
        //更新上架状态
        boolean update=lambdaUpdate()
                .eq(Serve::getId,id)
                .set(Serve::getSaleStatus,FoundationStatusEnum.DISABLE.getStatus())
                .update();
        if (!update){
            throw new ForbiddenOperationException("下架服务失败");
        }
        return baseMapper.selectById(id);
    }


    public void delete(Long id){
        //判断服务的id的有效性
        Serve serve=baseMapper.selectById(id);
        if (ObjectUtil.isNull(serve)){
            throw new ForbiddenOperationException("不存在该服务");
        }
        //判断当前服务id的上架状态
        if ((serve.getSaleStatus()==FoundationStatusEnum.ENABLE.getStatus() || serve.getSaleStatus()==FoundationStatusEnum.DISABLE.getStatus())){
            throw new ForbiddenOperationException("服务的当前状态不可删除");
        }
        //删除该服务
        int delete=baseMapper.deleteById(serve.getId());
        if (delete==0){
            throw new ForbiddenOperationException("删除失败");
        }
    }
}
