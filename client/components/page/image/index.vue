<template>
  <!-- 单图组：首页「通栏广告图」用它。
       dataList 元素字段：imgUrl（图片，必填）/ link（跳转地址，字符串路径或 {type:'PAGE'} 对象） -->
  <view class="diy-imageSingle" :style="{ paddingBottom: `${itemStyle.paddingTop}px`, background: itemStyle.background }">
    <view class="item-image" v-for="(dataItem, index) in dataList" :key="index" :style="{ padding: `${itemStyle.paddingTop}px ${itemStyle.paddingLeft}px 0` }">
      <view class="nav-to" @click="onLink(dataItem.link)">
        <image class="image" :src="dataItem.imgUrl" mode="widthFix"></image>
      </view>
    </view>
  </view>
</template>

<script>
  import mixin from '../mixin'

  export default {
    name: "Images",

    /**
     * 组件的属性列表
     * 用于组件自定义设置
     */
    props: {
      itemIndex: String,
      itemStyle: Object,
      params: Object,
      dataList: Array
    },

    mixins: [mixin],

    /**
     * 组件的方法列表
     * 更新属性和数据的方法与更新页面数据的方法类似
     */
    methods: {

      /**
       * 点击跳转。
       * 基类 mixin 的 onLink 只认 {type:'PAGE', param:{path,query}} 对象，
       * 而广告/轮播数据里的 url 是字符串路径，这里兼容两种写法。
       */
      onLink(linkObj) {
        if (!linkObj) {
          return false
        }
        if (typeof linkObj === 'string') {
          this.$navTo(linkObj)
          return true
        }
        if (linkObj.type === 'PAGE') {
          this.$navTo(linkObj.param.path, linkObj.param.query)
        }
        return true
      }
    }

  }
</script>

<style lang="scss" scoped>
  .diy-imageSingle .item-image .image {
    display: block;
    width: 100%;
  }
</style>
