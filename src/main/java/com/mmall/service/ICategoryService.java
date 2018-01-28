package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {

    /**
     * 创建品类
     *
     * @param category
     * @return
     */
    ServerResponse createCategory(Category category);

    /**
     * 更新品类信息
     *
     * @param category
     * @return
     */
    ServerResponse updateCategory(Category category);

    /**
     * 删除品类信息
     *
     * @param id
     * @return
     */
    ServerResponse deleteCategory(Integer id);

    /**
     * 根据id获取品类信息
     *
     * @param id
     * @return
     */
    ServerResponse getCategoryById(Integer id);

    /**
     * 根据id获取子级品类信息
     *
     * @param id
     * @return
     */
    ServerResponse getCategoryListByParentId(Integer id);

    /**
     * 根据parentId获取某一级品类信息
     *
     * @param parentId
     * @return
     */
    ServerResponse getLevelCategoryList(Integer parentId);

    /**
     * 根据parentId获取某一级品类及其子品类的id集合
     *
     * @param parentId
     * @return
     */
    ServerResponse<List<Integer>> selectCategoryAndChildrenIds(Integer parentId);
}
