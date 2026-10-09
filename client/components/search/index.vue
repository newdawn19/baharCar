<template>
  <!-- 搜索框 -->
  <view class="search-wrapper" :class="{ 'search-wrapper--inline': inline }">
    <view class="index-search" @click="onClick">
      <view class="index-cont-search t-c">
        <text class="search-icon iconfont icon-sousuo"></text>
        <text class="search-text">{{ tips }}</text>
      </view>
    </view>
  </view>
</template>

<script>
  export default {
    props: {
      tips: {
        type: String,
        default: '搜索关键字...'
      },
      itemStyle: Object,
      /**
       * 是否由外层容器负责定位。
       * 默认 false：本组件自己 fixed 悬浮（不占文档流，其它列表页沿用这套）；
       * 传 true：回到普通文档流，交给外层吸顶容器（首页 .index-sticky-header）统一吸顶。
       * fixed 不设 top 时会按「静态位置」锚定，前置高度一变就跑位，所以首页必须走 inline。
       */
      inline: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {}
    },

    methods: {
      onClick() {
        this.$emit('event')
      }
    }
  }
</script>

<style lang="scss" scoped>
  .search-wrapper {
    padding: 0rpx 10rpx 10rpx 10rpx;
    display: block;
    position: fixed;
    /* 关键：固定定位必须显式钉住 left/right。
       只写 width:100% 时，元素宽度按「视口」算，但 left 仍取静态位置，
       一旦外层有 margin/padding（例如 .bahar-card），就会恒定溢出那一段距离。 */
    left: 0;
    right: 0;
    z-index: 999999;
    background: #ffffff;
  }

  /* 首页方案：外层 .index-sticky-header 已整体 sticky，
     这里必须改回文档流，否则 fixed 不占位会遮挡下方焦点图、且吸顶锚点会漂移。 */
  .search-wrapper.search-wrapper--inline {
    position: static;
    /* 关键：必须是 auto 而不是 100%。100% + margin 会溢出 24rpx，
       auto 才会扣掉左右外边距，正好落在 12 ~ 363（与其它卡片同一条边）。 */
    width: auto;
    margin: 0 24rpx;
    /* 背景交给外层品牌色，避免头部出现大白块 */
    background: transparent;
  }

  .index-search {
    border-bottom: 0;
    background: #f5f5f5;
    border-radius: 100rpx;
    overflow: hidden;
    font-size: 28rpx;
    color: #6d6d6d;
    box-sizing: border-box;
    height: 82rpx;
    line-height: 82rpx;
    border: solid 2rpx #ffffff;
    text-align: left;
    display: block;
    margin-top: 10rpx;
    .index-cont-search {
      width: 100%;
      font-size: 28rpx;
      background: #f5f5f5;
      text-align: center;
      padding-left: 30rpx;
    }

    .index-cont-search .search-icon {
      font-size: 30rpx;
      font-weight: bold;
      float: left;
    }

    .index-cont-search .search-text {
      margin-left: 5rpx;
      text-align: center;
    }

  }
</style>
