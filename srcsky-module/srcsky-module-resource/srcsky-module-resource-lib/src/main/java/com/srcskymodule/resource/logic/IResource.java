package com.srcskymodule.resource.logic;

import com.srcskyframework.core.Enterprise;
import com.srcskyframework.dao.IBaseDao;
import com.srcskymodule.resource.domain.ResourceEntity;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-3-24
 * Time: 下午6:29
 * To change this template use File | Settings | File Templates.
 */
public interface IResource extends IBaseDao<ResourceEntity> {

    /**
     * 依照 状体 分组 统计 资源数量
     *
     * @return
     */
    public List<Enterprise> coungGroupByAuditing(Enterprise input);

    /**
     * 根据  哈希值 返回 对应 Resource
     *
     * @param hash
     * @return
     */
    public ResourceEntity findByHash(String hash);

    /**
     * 资源上传
     *
     * @return 对应数据库 的一条 资源信息
     */
    public List<ResourceEntity> fileuploader();

    /**
     * 截图
     * 根据 传入 资源编号 生成新传入的尺寸
     *
     * @return
     */
    public ResourceEntity thumbnail();

    /**
     * 同步资源到云空间
     */
    public void synchroToCloudSpace();

    /**
     * 同步资源到又拍云
     *
     * @param resourceEntity
     * @return
     * @throws Exception
     */
    public boolean synchroUpYun(ResourceEntity resourceEntity) throws Exception;

    /**
     * 又拍云通知 异步通知,
     *
     * @param input
     * @return
     */
    public ResourceEntity fileuploaderByNotify(Enterprise input);
}
