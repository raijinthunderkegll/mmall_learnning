package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import com.mmall.util.DataUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImple implements ICategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    /**
     * 创建品类
     *
     * @param category
     * @return
     */
    @Override
    public ServerResponse createCategory(Category category) {
        if (category != null && StringUtils.isNotBlank(category.getName())) {
            int count = categoryMapper.insert(category);
            if (count > 0) {
                return ServerResponse.createBySuccess("创建品类成功", category);
            }
        }
        return ServerResponse.createByErrorMessage("创建品类失败");
    }

    /**
     * 更新品类信息
     *
     * @param category
     * @return
     */
    @Override
    public ServerResponse updateCategory(Category category) {
        if (category != null && StringUtils.isNotBlank(category.getName())) {
            int count = categoryMapper.updateByPrimaryKeySelective(category);
            if (count > 0) {
                return ServerResponse.createBySuccess("修改品类信息成功", category);
            }
        }
        return ServerResponse.createByErrorMessage("修改品类信息失败");
    }

    /**
     * 删除品类信息
     *
     * @param id
     * @return
     */
    @Override
    public ServerResponse deleteCategory(Integer id) {

        if (id == 0) {
            return ServerResponse.createByErrorMessage("一级品类不可删除");
        }
        int count = categoryMapper.deleteByPrimaryKey(id);

        return count > 0 ? ServerResponse.createBySuccess("删除品类岑巩") : ServerResponse.createByErrorMessage("删除品类失败");
    }

    /**
     * 根据id获取品类信息
     *
     * @param id
     * @return
     */
    @Override
    public ServerResponse getCategoryById(Integer id) {
        Category category = categoryMapper.selectByPrimaryKey(id);

        if (DataUtil.isNotEmpty(category)) {
            return ServerResponse.createBySuccess("获取品类信息成功", category);
        }

        return ServerResponse.createByErrorMessage("获取品类信息失败");
    }

    /**
     * 根据id获取子级品类信息
     *
     * @param id
     * @return
     */
    @Override
    public ServerResponse getCategoryListByParentId(Integer id) {
        Set<Category> categories = getCategorySetRecursion(id);
        if(CollectionUtils.isEmpty(categories)){
            return ServerResponse.createByErrorMessage("未找到当前分类的子集");
        }
        return ServerResponse.createBySuccess(categories);
    }

    /**
     * 递归获取子集品类
     *
     * @param parentId
     * @return
     */
    private Set<Category> getCategorySetRecursion(Integer parentId){
        List<Category> categoryList = categoryMapper.selectPriorForChildren(parentId);
        if(CollectionUtils.isEmpty(categoryList)){
            return null;
        }
        Set<Category> categories = Sets.newConcurrentHashSet(categoryList);
        return categories;
    }

    /**
     * 根据parentId获取某一级品类信息
     *
     * @param parentId
     * @return
     */
    @Override
    public ServerResponse getLevelCategoryList(Integer parentId) {

        List<Category> categoryList = categoryMapper.selectLevelForChildren(parentId);
        if(CollectionUtils.isEmpty(categoryList)){
            return ServerResponse.createByErrorMessage("未找到当前分类");
        }
        Set<Category> categories = Sets.newConcurrentHashSet(categoryList);
        return ServerResponse.createBySuccess(categories);
    }

    /**
     * 根据parentId获取某一级品类及其子品类的id集合
     *
     * @param parentId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenIds(Integer parentId) {
        Set<Category> categorySet = Sets.newHashSet();
        getCategorySetRecursion(parentId);


        List<Integer> categoryIdList = Lists.newArrayList();
        if(parentId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }
}
