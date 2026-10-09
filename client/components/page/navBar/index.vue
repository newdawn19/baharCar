<template>
  <!-- 导航组 -->
  <view class="diy-navBar bahar-anim" :style="{ background: itemStyle.background, color: itemStyle.textColor }">
    <view class="data-list" :class="[`avg-sm-${itemStyle.rowsNum || 4}`]">
      <view class="item-nav" v-for="(dataItem, index) in dataList" :key="index">
        <view class="nav-to bahar-press" :class="'nav-' + index" @click="onLink(dataItem.linkUrl)">
          <view class="item-image">
            <image class="image" mode="aspectFit" :src="dataItem.imgUrl"></image>
          </view>
          <view class="item-text onelist-hidden">
             <view class="text">{{ dataItem.text }}</view>
             <view class="tip">{{ dataItem.tip ? dataItem.tip : '' }}</view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  import mixin from '../mixin'

  export default {
    name: "NavBar",
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
        onLink(linkObj) {
            const app = this;
            // 汽车行业化分支：'vehicleScan' 不是路由，是「扫码进入车辆订单」的约定值。
            // 零售版没有这段，同步样式时不能覆盖掉，否则汽修扫码入口失效。
            if (linkObj == 'vehicleScan') {
                uni.scanCode({
                    success:function(res){
                       app.$navTo('pages/vehicle/order?code=' + res.result);
                    }
                });
            } else {
                 app.$navTo(linkObj);
            }
        }
    }

  }
</script>

<style lang="scss" scoped>
  /* 卡片容器：取代旧的 float 分列写法。
     外边距刻意与全局 .bahar-card 同为 24rpx —— 差哪怕 2px，
     同一屏里四宫格卡与商品/优惠券卡的左右边缘就对不齐（用户报的就是"左右不对齐"）。 */
  .diy-navBar {
    background: #ffffff;
    margin: 24rpx;
    padding: 6rpx;
    border-radius: 20rpx;
    border: 1rpx solid #e6e6e6;
    box-sizing: border-box;
  }

  .diy-navBar .data-list {
    display: flex;
    flex-wrap: wrap;
  }

  .item-nav {
    box-sizing: border-box;
    padding: 2rpx;
    text-align: center;
    .nav-to {
        margin: 0;
        padding: 16rpx 8rpx 12rpx 8rpx;
        border-radius: 12rpx;
        min-height: 120rpx;
        box-sizing: border-box;
        background: linear-gradient(to bottom, #ffffff 0%, #f5f5f5 100%);
        border: 1rpx solid #e5e5e5;
        transition: transform 0.15s ease, background 0.15s ease;
    }
    .nav-to:active {
        transform: scale(0.96);
        background: #f0f5f5;
    }
    .item-text {
      padding: 0 4rpx;
      .text {
          font-size: 24rpx;
          color: #333;
          font-weight: 600;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
      }
      .tip {
          font-size: 20rpx;
          margin-top: 4rpx;
          color: #333;
          opacity: 0.8;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
      }
    }

    .item-image {
      margin: 0 auto;
      font-size: 0;
      width: 72rpx;
      height: 72rpx;
      box-sizing: border-box;
    }

    .item-image .image {
      width: 72rpx;
      height: 72rpx;
      border-radius: 16rpx;
    }

  }

  /* 分列布局（flex 等分，默认 4 列） */
  .diy-navBar .avg-sm-3>.item-nav {
    flex: 0 0 33.33333333%;
  }

  .diy-navBar .avg-sm-4>.item-nav {
    flex: 0 0 25%;
  }

  .diy-navBar .avg-sm-2>.item-nav {
    flex: 0 0 50%;
  }
</style>
