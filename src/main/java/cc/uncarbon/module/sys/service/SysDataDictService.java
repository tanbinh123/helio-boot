package cc.uncarbon.module.sys.service;

import cc.uncarbon.framework.core.constant.HelioConstant;
import cc.uncarbon.framework.core.exception.BusinessException;
import cc.uncarbon.framework.core.page.PageParam;
import cc.uncarbon.framework.core.page.PageResult;
import cc.uncarbon.framework.crud.service.impl.HelioBaseServiceImpl;
import cc.uncarbon.module.sys.annotation.SysLog;
import cc.uncarbon.module.sys.entity.SysDataDictEntity;
import cc.uncarbon.module.sys.enums.SysErrorEnum;
import cc.uncarbon.module.sys.mapper.SysDataDictMapper;
import cc.uncarbon.module.sys.model.request.AdminInsertOrUpdateSysDataDictDTO;
import cc.uncarbon.module.sys.model.request.AdminListSysDataDictDTO;
import cc.uncarbon.module.sys.model.response.SysDataDictBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 数据字典
 *
 * @author Uncarbon
 */
@Slf4j
@Service
public class SysDataDictService extends HelioBaseServiceImpl<SysDataDictMapper, SysDataDictEntity> {

    /**
     * 后台管理-分页列表
     */
    public PageResult<SysDataDictBO> adminList(PageParam pageParam, AdminListSysDataDictDTO dto) {
        Page<SysDataDictEntity> entityPage = this.page(
                new Page<>(pageParam.getPageNum(), pageParam.getPageSize()),
                new QueryWrapper<SysDataDictEntity>()
                        .lambda()
                        // 参数描述
                        .like(StrUtil.isNotBlank(dto.getDescription()), SysDataDictEntity::getDescription, StrUtil.cleanBlank(dto.getDescription()))
                        // 排序
                        .orderByDesc(SysDataDictEntity::getCreatedAt)
        );

        return this.entityPage2BOPage(entityPage);
    }

    /**
     * 通用-详情
     *
     * @deprecated 使用 getOneById(java.lang.Long, boolean) 替代
     */
    @Deprecated
    public SysDataDictBO getOneById(Long entityId) throws BusinessException {
       return this.getOneById(entityId, true);
    }

    /**
     * 通用-详情
     *
     * @param entityId 实体类主键ID
     * @param throwIfInvalidId 是否在 ID 无效时抛出异常
     * @return null or BO
     */
    public SysDataDictBO getOneById(Long entityId, boolean throwIfInvalidId) throws BusinessException {
        SysDataDictEntity entity = this.getById(entityId);
        if (throwIfInvalidId) {
            SysErrorEnum.INVALID_ID.assertNotNull(entity);
        }

        return this.entity2BO(entity);
    }

    /**
     * 后台管理-新增
     */
    @SysLog(value = "新增数据字典")
    @Transactional(rollbackFor = Exception.class)
    public Long adminInsert(AdminInsertOrUpdateSysDataDictDTO dto) {
        log.info("[后台管理-新增数据字典] >> DTO={}", dto);
        this.checkExistence(dto);

        dto.setId(null);
        SysDataDictEntity entity = new SysDataDictEntity();
        BeanUtil.copyProperties(dto, entity);

        this.save(entity);

        return entity.getId();
    }

    /**
     * 后台管理-编辑
     */
    @SysLog(value = "编辑数据字典")
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdate(AdminInsertOrUpdateSysDataDictDTO dto) {
        log.info("[后台管理-编辑数据字典] >> DTO={}", dto);
        this.checkExistence(dto);

        SysDataDictEntity entity = new SysDataDictEntity();
        BeanUtil.copyProperties(dto, entity);

        this.updateById(entity);
    }

    /**
     * 后台管理-删除
     */
    @SysLog(value = "删除数据字典")
    @Transactional(rollbackFor = Exception.class)
    public void adminDelete(Collection<Long> ids) {
        log.info("[后台管理-删除数据字典] >> ids={}", ids);
        this.removeByIds(ids);
    }


    /*
    私有方法
    ------------------------------------------------------------------------------------------------
     */

    private SysDataDictBO entity2BO(SysDataDictEntity entity) {
        if (entity == null) {
            return null;
        }

        SysDataDictBO bo = new SysDataDictBO();
        BeanUtil.copyProperties(entity, bo);

        // 可以在此处为BO填充字段

        return bo;
    }

    private List<SysDataDictBO> entityList2BOs(List<SysDataDictEntity> entityList) {
        // 深拷贝
        List<SysDataDictBO> ret = new ArrayList<>(entityList.size());
        entityList.forEach(
                entity -> ret.add(this.entity2BO(entity))
        );

        return ret;
    }

    private PageResult<SysDataDictBO> entityPage2BOPage(Page<SysDataDictEntity> entityPage) {
        PageResult<SysDataDictBO> ret = new PageResult<>();
        BeanUtil.copyProperties(entityPage, ret);
        ret.setRecords(this.entityList2BOs(entityPage.getRecords()));

        return ret;
    }

    /**
     * 检查是否已存在相同数据
     *
     * @param dto DTO
     */
    private void checkExistence(AdminInsertOrUpdateSysDataDictDTO dto) {
        SysDataDictEntity existingEntity = this.getOne(
                new QueryWrapper<SysDataDictEntity>()
                        .lambda()
                        // 仅取主键ID
                        .select(SysDataDictEntity::getId)
                        // 驼峰式键名相同
                        .eq(SysDataDictEntity::getCamelCaseKey, dto.getCamelCaseKey())
                        .or()
                        // 或帕斯卡式键名相同
                        .eq(SysDataDictEntity::getPascalCaseKey, dto.getPascalCaseKey())
                        .or()
                        // 或下划线式键名相同
                        .eq(SysDataDictEntity::getUnderCaseKey, dto.getUnderCaseKey())
                        .last(HelioConstant.CRUD.SQL_LIMIT_1)
        );

        if (existingEntity != null && !existingEntity.getId().equals(dto.getId())) {
            throw new BusinessException(400, "已存在相同数据字典，请重新输入");
        }
    }

}
