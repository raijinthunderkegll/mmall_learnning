package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ICategoryService iCategoryService;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if(product == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //将第一张子图设置为主图
        if(StringUtils.isNotBlank(product.getSubImages())){
            String[] subImageArray = product.getSubImages().split(",");
            product.setMainImage(subImageArray[0]);
        }
        //id == null insert / id != null update
        if(product.getId() == null){
            int rowCount = productMapper.insert(product);
            if(rowCount > 0){
                return ServerResponse.createBySuccess("新增产品成功");
            }
            return ServerResponse.createByErrorMessage("新增产品失败");
        }else{
            int rowCount = productMapper.updateByPrimaryKey(product);
            if(rowCount > 0){
                return ServerResponse.createBySuccess("更新产品成功");
            }
            return ServerResponse.createByErrorMessage("更新产品失败");
        }
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowcount = productMapper.updateByPrimaryKeySelective(product);
        if(rowcount > 0){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);
        productDetailVo.setImageHost(PropertiesUtil.getProperty(Const.FTP_PREFIX, Const.IMG_DEFAULT_URL));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToString(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToString(product.getUpdateTime()));
        return productDetailVo;
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.selectList();
        List<ProductListVo> productListVos = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(products)){
            for(Product productItem : products){
                ProductListVo productListVo = assembleProductListVo(productItem);
                productListVos.add(productListVo);
            }
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductListVo assembleProductListVo(Product productItem) {
        ProductListVo productListVo = new ProductListVo();
        BeanUtils.copyProperties(productItem,productListVo);
        productListVo.setImageHost(PropertiesUtil.getProperty(Const.FTP_PREFIX,Const.IMG_DEFAULT_URL));
        return productListVo;
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.selectByProductNameOrId(productName,productId);
        List<ProductListVo> productListVos = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(products)){
            for(Product productItem : products){
                ProductListVo productListVo = assembleProductListVo(productItem);
                productListVos.add(productListVo);
            }
        }
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null || product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIds = Lists.newArrayList();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            //无此分类 且 无关键字，返回空结果集
            if(category == null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductDetailVo> productDetailVos = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productDetailVos);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIds = iCategoryService.selectCategoryAndChildrenIds(categoryId).getData();
        }

        PageHelper.startPage(pageNum,pageSize,orderBy);
        List<Product> products = productMapper.selectByNameAndCategoryIds(keyword,categoryIds);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : products){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
