<template>
  <!-- 卡券组：首页「优惠券领取区」用的横向券卡。
       与旧版（列表式、点击整卡跳详情）的区别：
         1) 横向滚动，一屏能看到 2 张以上，对齐竞品首页形态；
         2) 「立即领取」是**真按钮**，调用 clientApi/coupon/receive 直接入账，
            成功后按钮变「已领取」，不必再跳详情页。
       面额 / 门槛 / 适用范围 三行全部来自后端字段，不写死：
         面额 = amount，门槛 = sellingPoint（后端按 outRule 算），范围 = description。 -->
  <view class="diy-coupon" :style="{ background: itemStyle.background }">
    <scroll-view class="coupon-scroll" scroll-x :show-scrollbar="false">
      <view class="coupon-row">
        <view class="coupon-card bahar-press"
              v-for="(dataItem, index) in dataList"
              :key="index"
              @click="onTargetCoupon(dataItem.id, dataItem.type, dataItem.userCouponId)">
          <view class="coupon-main">
            <view class="coupon-left">
              <view class="coupon-amount" v-if="dataItem.amount > 0">
                <text class="unit">¥</text><text class="num">{{ formatAmount(dataItem.amount) }}</text>
              </view>
              <view class="coupon-amount coupon-amount--name" v-else>
                <text class="name">{{ dataItem.name }}</text>
              </view>
              <view class="coupon-rule">{{ dataItem.sellingPoint ? dataItem.sellingPoint : '' }}</view>
              <view class="coupon-range">{{ rangeText(dataItem) }}</view>
            </view>
            <view class="coupon-split"></view>
            <view class="coupon-right">
              <!-- 优惠券：可领取 / 已领取 -->
              <view v-if="dataItem.type === 'C'"
                    class="coupon-btn"
                    :class="{ 'coupon-btn--state': dataItem.isReceive }"
                    @click.stop="onReceive(dataItem)">
                {{ dataItem.isReceive ? '已领取' : '立即领取' }}
              </view>
              <!-- 储值卡 -->
              <view v-else-if="dataItem.type === 'P'" class="coupon-btn">立即预存</view>
              <!-- 计次卡 -->
              <view v-else class="coupon-btn"
                    :class="{ 'coupon-btn--state': dataItem.isReceive }">
                {{ dataItem.isReceive ? '已领取' : '领取次卡' }}
              </view>
            </view>
          </view>
          <view class="coupon-stock" v-if="dataItem.leftNum >= 0">
            <text>剩余{{ dataItem.leftNum }}张</text>
          </view>
        </view>
        <view class="coupon-tail" v-if="dataList.length === 0"></view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
  import * as couponApi from '@/api/coupon'
  import { checkLogin } from '@/utils/app'

  export default {
    name: "Coupon",

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

    /**
     * 组件的方法列表
     * 更新属性和数据的方法与更新页面数据的方法类似
     */
    methods: {

      /**
       * 面额展示：整数不带小数，避免 "¥100.00" 撑破卡片
       */
      formatAmount(amount) {
        const num = Number(amount)
        if (isNaN(num)) {
          return '0'
        }
        return num % 1 === 0 ? String(num) : num.toFixed(2)
      },

      /**
       * 适用范围：后端把范围写在 description（全场通用 / 指定商品可用）
       */
      rangeText(dataItem) {
        const text = dataItem.description ? String(dataItem.description).trim() : ''
        if (!text || text === dataItem.name) {
          return '全场通用'
        }
        return text
      },

      /**
       * 领取优惠券（真入账）
       * @param {Object} dataItem 当前券
       */
      onReceive(dataItem) {
        const app = this
        if (dataItem.isReceive) {
          return
        }
        if (!checkLogin()) {
          uni.showModal({
            title: '温馨提示',
            content: '登录后才能领取优惠券',
            confirmText: '去登录',
            cancelText: '再逛逛',
            success(res) {
              if (res.confirm) {
                uni.navigateTo({ url: '/pages/login/index' })
              }
            }
          })
          return
        }
        // 加载动画与失败提示都由 request 插件负责（默认 isPrompt/load 都是 true），
        // 这里不再自己 showLoading，避免两层 loading 互相顶掉。
        couponApi.receive({ couponId: dataItem.id })
          .then(() => {
            // 就地更新（dataList 由父级传入，改对象属性即可生效）
            app.$set(dataItem, 'isReceive', true)
            app.$set(dataItem, 'leftNum', Math.max(0, (dataItem.leftNum || 0) - 1))
            uni.showToast({ title: '领取成功', icon: 'success' })
            app.$emit('receive', dataItem)
          })
          .catch(() => {
            // request 插件已按接口返回的 message 弹过提示
          })
      },

      /**
       * 卡券详情页（点卡片空白处）
       */
      onTargetCoupon(couponId, type, userCouponId) {
        if (type === 'P') {
          this.$navTo(`subPages/prestore/buy`, { couponId })
        } else if (type === 'C') {
          this.$navTo(`subPages/coupon/detail`, { couponId: couponId, userCouponId: userCouponId })
        } else if (type === 'T') {
          this.$navTo(`subPages/timer/detail`, { couponId: couponId, userCouponId: userCouponId })
        }
      }
    }
  }
</script>

<style lang="scss" scoped>
  /* 卡片容器：外边距由首页的 .bahar-card 统一给，这里只负责横向排布 */
  .diy-coupon {
    width: 100%;
    box-sizing: border-box;

    .coupon-scroll {
      width: 100%;
      white-space: nowrap;
    }

    .coupon-row {
      display: inline-block;
      white-space: nowrap;
      padding: 4rpx 0 8rpx;
      /* 消掉 inline-block 之间的空白字符，卡片间距只由 margin-right 决定，
         这样「一屏正好两张」的宽度是可算的 */
      font-size: 0;
    }

    /* 横向券卡：卡片宽度按「首页卡片内可用宽度 654rpx 正好放两张」反推 */
    .coupon-card {
      display: inline-block;
      vertical-align: top;
      width: 310rpx;
      min-height: 160rpx;
      margin-right: 16rpx;
      padding: 20rpx 16rpx 12rpx;
      box-sizing: border-box;
      background: #ffffff;
      border: 1rpx solid #e6e6e6;
      border-radius: 16rpx;
      white-space: normal;

      &:last-child {
        margin-right: 0;
      }
    }

    .coupon-main {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .coupon-left {
      flex: 1;
      min-width: 0;
      overflow: hidden;
    }

    .coupon-amount {
      color: #fa5151;
      font-weight: 700;
      font-size: 40rpx;
      line-height: 1.1;

      .unit {
        font-size: 24rpx;
      }

      .num {
        font-size: 40rpx;
      }

      /* 面额为 0 的券直接显示券名，避免出现 ¥0 */
      &--name {
        color: #333333;

        .name {
          font-size: 28rpx;
        }
      }
    }

    .coupon-rule {
      margin-top: 6rpx;
      font-size: 22rpx;
      color: #666666;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }

    .coupon-range {
      margin-top: 4rpx;
      font-size: 20rpx;
      color: #666666;
      opacity: 0.75;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }

    /* 左信息 / 右按钮之间的虚线 */
    .coupon-split {
      align-self: stretch;
      margin: 0 14rpx;
      border-left: 1rpx dashed #e6e6e6;
    }

    .coupon-right {
      flex-shrink: 0;
      display: flex;
      align-items: center;
    }

    .coupon-btn {
      height: 48rpx;
      line-height: 48rpx;
      padding: 0 16rpx;
      border-radius: 26rpx;
      font-size: 20rpx;
      color: #ffffff;
      background: $bahar-theme;
      text-align: center;
      transition: transform 0.15s ease, background 0.15s ease;

      &:active {
        transform: scale(0.94);
      }

      /* 已领取 / 不可领取 */
      &--state {
        color: #cccccc;
        background: #f5f5f5;
      }
    }

    .coupon-stock {
      margin-top: 10rpx;
      font-size: 20rpx;
      color: #999999;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }
</style>
