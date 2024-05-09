package com.jzo2o.foundations.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务类
 */

public interface IServeService extends IService<Serve> {

    /**
     * 分页查询服务列表
     * @param servePageQueryReqDTO 查询条件
     * @return 分页结果
     */
    PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO);


    /**
     * 批量新增
     * @param serveUpsertReqDTOS 批量新增数据
     */
    void batchAdd(List<ServeUpsertReqDTO> serveUpsertReqDTOS);

    /**
     * 更新价格
     * @param id 服务id
     * @param price
     */
    Serve update(Long id, BigDecimal price);


    /**
     * 区域上架服务
     * @param id
     * @return
     */
    Serve onSale(Long id);

    /**
     * 区域服务下架
     * @param id
     * @return
     */
    Serve offSale(Long id);

    /**
     * 删除区域服务
     * @param id
     */
    void delete(Long id);
}
