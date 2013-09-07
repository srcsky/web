package com.srcskymodule.resource.logic.impl;

import com.srcskyframework.core.Configuration;
import com.srcskyframework.core.Constants;
import com.srcskyframework.core.Enterprise;
import com.srcskyframework.dao.HibernateBaseDaoImpl;
import com.srcskyframework.exception.LogicException;
import com.srcskyframework.helper.*;
import com.srcskymodule.resource.domain.ResourceEntity;
import com.srcskymodule.resource.logic.IResource;
import com.srcskymodule.resource.support.UpYun;
import org.apache.commons.fileupload.FileItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-3-24
 * Time: 下午6:28
 * To change this template use File | Settings | File Templates.
 */

@Repository
public class ResourceImpl extends HibernateBaseDaoImpl<ResourceEntity> implements IResource {

    private static final byte[] lock = new byte[]{};

    /**
     * 生成 SQL 语句
     *
     * @param params
     * @return
     */
    public StringBuffer generateSQL(final ResourceEntity params) {
        return builderSql(params, new IBuilderSql() {
            public boolean execute(StringBuffer queryString, String simpleName) {
                if (!params.isEmpty("hash")) {
                    append(queryString, "and", simpleName, "hash=:hash");
                }
                if (!params.isEmpty("synchrostatus")) {
                    append(queryString, "and", simpleName, "synchrostatus=:synchrostatus");
                }
                if (params.getInt("isimage") > 0) {
                    append(queryString, "and", simpleName, "isimage=:isimage");
                }
                if (params.getInt("origin") > 0) {
                    append(queryString, "and", simpleName, "origin=:origin");
                }

                if (params.getInt("origin") > 0) {
                    append(queryString, "and", simpleName, "origin=:origin");
                }

                if (params.getInt("category_not_in") != 0) {
                    append(queryString, "and", simpleName, "category<>" + params.get("category_not_in"));
                }

                if (!params.isEmpty("likevalue")) {
                    append(queryString, " AND (");
                    if (params.isNumber("likevalue")) {
                        append(queryString, simpleName + ".id='" + params.get("likevalue") + "' OR");
                    }
                    append(queryString, simpleName + ".hash='" + params.get("likevalue") + "' OR");
                    append(queryString, simpleName + ".title like '%" + params.get("likevalue") + "%' OR");
                    append(queryString, simpleName + ".path like '%" + params.get("likevalue") + "%'");
                    append(queryString, " )");
                }
                return false;
            }
        });
    }

    /**
     * 依照 状体 分组 统计 资源数量
     *
     * @return
     */
    public List<Enterprise> coungGroupByAuditing(Enterprise input) {
        String queryString = jdbcLogic.getQueryString(ResourceImpl.class, "coungGroupByAuditing");
        String conditions = "1=1 ";
        if (!input.isEmpty("likevalue")) {
            conditions += " AND (";
            if (input.isNumber("likevalue")) {
                conditions += " T1.id = '" + input.getString("likevalue") + "' OR ";
            }
            conditions += " T1.hash = '" + input.getString("likevalue") + "' OR ";
            conditions += " T1.title like '%" + input.getString("likevalue") + "%' OR ";
            conditions += " T1.path like '%" + input.getString("likevalue") + "%' ";
            conditions += ") ";
        }
        if (input.getInt("category_not_in") != 0) {
            conditions += " AND (";
            conditions += " T1.category <>" + input.get("category_not_in");
            conditions += ") ";
        }
        queryString = queryString.replace("1=1", conditions);
        return jdbcLogic.finds(queryString);
    }

    /*@Cacheable(value = "com.enterprise.application.system.logic.impl.ResourceImpl", key = "'findById('+#id+')'")*/
    public ResourceEntity findById(Serializable id) {
        return super.findById(id);
    }

    /*@Cacheable(value = "com.enterprise.application.system.logic.impl.ResourceImpl", key = "'findByHash('+#hash+')'")*/
    public ResourceEntity findByHash(String hash) {
        AssertHelper.isEmpty(hash, "参数不能为中，hash=" + hash);
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setHash(hash);
        return find(resourceEntity);
    }

    /**
     * 文件上传
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ResourceEntity> fileuploader() {
        List<ResourceEntity> resourceEntityList = new ArrayList<ResourceEntity>();
        Enterprise input = WebHelper.getInput();
        if (!input.isEmpty("fileuploader")) {
            try {
                List<FileItem> fileItems = new ArrayList<FileItem>();
                if (input.get("fileuploader") instanceof ArrayList) {
                    fileItems.addAll(input.getList("fileuploader"));
                } else {
                    fileItems.add((FileItem) input.get("fileuploader"));
                }
                ResourceEntity resourceEntity = null;
                for (FileItem fileItem : fileItems) {
                    File temp = FileHelper.writeFileUploader(fileItem);
                    input.put("length", temp.length());
                    /* 为文件生成Hash*/
                    String hash = Md5Helper.getFileMD5String(temp);
                    /*检测库内是否存在相同 的Hash,有则 重用，无则 新增*/
                    resourceEntity = new ResourceEntity();
                    resourceEntity.setHash(hash);
                    resourceEntity = this.find(resourceEntity);

                    if (resourceEntity == null) {
                        resourceEntity = new ResourceEntity();
                        resourceEntity.setHash(hash);
                        resourceEntity.setLength(temp.length());
                        resourceEntity.setCreate(new Date());
                        resourceEntity.setTitle(fileItem.getName());
                        resourceEntity.setPostfix(FileHelper.getPostfix(temp.getName()));
                        resourceEntity.setOrigin(Constants.RESOURCE_ORIGIN_UPLOADER);
                        resourceEntity.setAuditing(Constants.AUDITING_STATUS_START);
                        logger.debug(Constants.LINE);
                        logger.debug("FileUplaoder  getDimension Start -------------");
                        Dimension dimension = null;//ImageMagick.getDimension(temp.getAbsolutePath());
                        logger.debug("FileUplaoder  getDimension End -------------");
                        logger.debug(Constants.LINE);
                        if (null == dimension) {
                            resourceEntity.setIsimage(Constants.NO);
                        } else {
                            resourceEntity.setIsimage(Constants.YES);
                            resourceEntity.setWidth((int) dimension.getWidth());
                            resourceEntity.setHeight((int) dimension.getHeight());
                        }
                        resourceEntity.setSynchrostatus(Constants.STATUS_PROCCESS_INIT);
                        resourceEntity.setCategory(input.getLong("category"));
                        this.update(resourceEntity);
                        resourceEntity.setPath(resourceEntity.toString());
                        this.update(resourceEntity);
                        /*转移文件 到正式的目录*/
                        FileHelper.moveFile(temp, FileHelper.generateDirectoryAbsoluteFull(resourceEntity.getId()));
                        //FileUtility.moveFile(resourceEntity, temp);
                    } else {
                        FileHelper.delFile(temp);
                    }
                    resourceEntity.set("filepath", resourceEntity.toString());
                    resourceEntityList.add(resourceEntity);
                }
            } catch (Exception ex) {
                throw new LogicException("文件上传失败：" + ex.getMessage());
            } finally {
                /*删除临时文件*/
                Object[] keyArray = input.keySet().toArray();
                for (Object field : keyArray) {
                    if (input.get(field) instanceof FileItem) {
                        ((FileItem) input.get(field)).delete();
                        input.remove(field);
                    }
                }
            }
            return resourceEntityList;
        } else {
            throw new LogicException("文件上传失败：fileuploader字段不能为空！");
        }
    }

    public ResourceEntity thumbnail() {
        Enterprise input = WebHelper.getInput();
        ResourceEntity resourcesEntity = null;
        if (input.isEmpty("size"))
            throw new LogicException("截图失败：新尺寸(size)不能为空");
        if (input.isEmpty("id") || null == (resourcesEntity = this.get(ResourceEntity.class, input.getLong("id"))))
            throw new LogicException("截图失败：资源编号(id)不能为空");

        File file = FileHelper.generateDirectoryAbsoluteFull(input.getLong("id"));
        AssertHelper.isTrue(!file.exists(), "请求图片已被删除");

        String[] size = input.getString("size").replace("s", "").split("x");
        String generateFile = new String(file.getPath());
        int width = 0;
        int height = 0;
        if (size.length == 2) {//限宽，限高
            width = Integer.valueOf(size[0]);
            height = Integer.valueOf(size[1]);
            generateFile += "_" + size[0] + "x" + size[1];
        } else {
            generateFile += "_" + size[0];
            if (size[0].startsWith("h")) {
                height = Integer.valueOf(size[0].toUpperCase().replace("H", ""));
            } else {
                width = Integer.valueOf(size[0].toUpperCase().replace("W", ""));
            }
        }
        File outputFile = new File(generateFile.toString());
        if (!outputFile.exists()) {
            synchronized (lock) {
                //ImageMagick.resizeImg(file.getPath(), outputFile.toString(), width, height, 0, 0, 0, 0);
            }
        }
        resourcesEntity.set("filepath", outputFile);
        return resourcesEntity;
    }


    /**
     * 同步资源到云空间
     * 移动文件
     * 如果启用云存储  则上传 到云服务器上一份
     */
    @Transactional(noRollbackFor = LogicException.class)
    public void synchroToCloudSpace() {

        //开启同步的文件类型
        boolean imgSynchro = Configuration.get().getBoolean("spring.job.resource.upyun.space.img.enable");
        boolean fileSynchro = Configuration.get().getBoolean("spring.job.resource.upyun.space.file.enable");

        //启用同步并且 图片空间 与文件空间至少 有一个开启
        if (!Configuration.get().getBoolean("spring.job.resource.upyun.debug") && (imgSynchro || fileSynchro)) {
            //查询出 本地上传并且是未做过 同步的 资源文件
            ResourceEntity parms = new ResourceEntity();
            parms.set(Pagination.PAGE_SIZE, Configuration.get().getInt("spring.job.resource.size"));
            parms.setSynchrostatus(Constants.STATUS_PROCCESS_INIT);
            parms.setOrigin(Constants.RESOURCE_ORIGIN_UPLOADER);
            parms.setAuditing(Constants.AUDITING_STATUS_SUCCESS);
            //前置条件已经定义至少有一种 空间类型 允许同步，逻辑才能走到这里
            if (!imgSynchro) {
                parms.setIsimage(Constants.NO);
            } else if (!fileSynchro) {
                parms.setIsimage(Constants.YES);
            }
            List<ResourceEntity> resourceEntityList = finds(parms);
            if (!isEmpty(resourceEntityList)) {
                logger.info("发现(" + resourceEntityList.size() + ")个新文件需要同步到云服务器！");
                for (ResourceEntity resourceEntity : resourceEntityList) {
                    resourceEntity.setSynchrostatus(Constants.STATUS_PROCCESS_IN);
                    update(resourceEntity);
                    try {
                        synchroUpYun(resourceEntity);
                        resourceEntity.setSynchrostatus(Constants.STATUS_PROCCESS_SUCCESS);
                    } catch (LogicException ex) {
                        resourceEntity.setSynchrostatus(Constants.STATUS_PROCCESS_FAIL);
                    }
                    update(resourceEntity);
                }
            }
        }
    }

    /**
     * 同步资源到又拍云
     *
     * @param resourceEntity
     * @return
     */
    public boolean synchroUpYun(ResourceEntity resourceEntity) {
        File source = FileHelper.generateDirectoryAbsoluteFull(resourceEntity.getId());//, resourceEntity.getPostfix()
        //云存储上传
        String filepath = FileHelper.generateDirectory(resourceEntity.getId()) + resourceEntity.getId();//+ "." + resourceEntity.getPostfix()
        try {
            if (resourceEntity.getIsimage() == Constants.YES) {
                if (Configuration.get().getBoolean("spring.job.resource.upyun.space.img.enable")) {
                    String space = Configuration.get().getString("spring.job.resource.upyun.space.img");
                    String username = Configuration.get().getString("spring.job.resource.upyun.space.img.username");
                    String password = Configuration.get().getString("spring.job.resource.upyun.space.img.password");
                    UpYun upYun = new UpYun(space, username, password);
                    upYun.setFileSecret(resourceEntity.getHash());
                    if (!upYun.writeFile("/static/fileuploader/img/" + filepath, source, true)) {
                        throw new Exception("上传失败");
                    }
                }
            } else {
                if (Configuration.get().getBoolean("spring.job.resource.upyun.space.file.enable")) {
                    String space = Configuration.get().getString("spring.job.resource.upyun.space.file");
                    String username = Configuration.get().getString("spring.job.resource.upyun.space.file.username");
                    String password = Configuration.get().getString("spring.job.resource.upyun.space.file.password");
                    UpYun upYun = new UpYun(space, username, password);
                    if (!upYun.writeFile("/static/fileuploader/file/" + filepath, source, true)) {
                        throw new Exception("上传失败");
                    }
                }
            }
        } catch (Exception ex) {
            throw LogicException.generateException("同步文件到 又拍云发生异常:" + ex.getMessage() + "," + resourceEntity);
        }
        return true;
    }


    /**
     * 又拍云通知 异步通知,
     *
     * @param input
     * @return
     */
    public ResourceEntity fileuploaderByNotify(Enterprise input) {
        String url = input.getString("url");
        String[] url_params = url.split("/");
        String md5 = url_params[4].substring(0, url_params[4].lastIndexOf("."));
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setHash(md5);
        resourceEntity = find(resourceEntity);
        //检测 文件是否已存在
        if (null == resourceEntity) {
            //创建新资源入库
            //图片类型
            if (input.isEmpty("image-type")) {
                resourceEntity.setIsimage(Constants.YES);
                resourceEntity.setWidth(input.getInt("image-width"));
                resourceEntity.setHeight(input.getInt("image-height"));
            } else {
                resourceEntity.setIsimage(Constants.NO);
            }

            resourceEntity.setHash(md5);

            //备份文件到本地
            //spring.job.resource.upyun.space.img.domain
            /*resourceEntity.setLength();*/
        } else {
            //资源已经存在。通知通知 upyun 删除已上传资源
        }
        /*image-height	|        737
          image-frames	|        1
          code			|        200
          url			|        /fileuploader/img/2012/07/b4a37068072ca199a4f17fc65fba130d.jpg
          sign			|        a034938e848c942ea8c243260cf93892
          message		|        ok
          image-width	|        550
          time			|        1342774322
          image-type		|        JPEG*/

        //filepath
        //resourceEntity.set("filepath", )
        return null;
    }


    public static void main(String[] args) {
        File file = FileHelper.generateDirectoryAbsoluteFull(3l, "jpg");
        //ImageMagick.resizeImg(file.getPath(), new StringBuffer(file.getPath()).insert(file.getPath().indexOf(".jpg"), "_100x100").toString(), 100, 100, 0, 0, 0, 0);
        System.out.println(file.exists());
    }

}
