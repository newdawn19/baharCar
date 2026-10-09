<template>
  <view v-if="!isLoading" class="container">
    <!-- 会员 Hero 卡 -->
    <view class="bahar-hero bahar-anim">
      <view class="bahar-hero__top">
        <view class="bahar-hero__avatar bahar-press" @click="onUserInfo">
          <image class="image" :src="userInfo.avatar ? userInfo.avatar : '/static/default-avatar.png'"></image>
        </view>
        <view class="bahar-hero__user" @click="onUserInfo">
          <view class="bahar-hero__name">{{ userInfo.name ? userInfo.name : '未登录'}}</view>
          <view class="bahar-hero__sub" v-if="!isLogin">点击登录，尊享会员特权</view>
          <view v-if="userInfo.gradeId > 0 && gradeInfo" class="bahar-hero__grade">
            <text>{{ gradeInfo.name }}</text>
            <text v-if="gradeEndTime" style="margin-left:12rpx;opacity:.8;">{{ gradeEndTime }}</text>
          </view>
          <view v-else class="bahar-hero__sub">{{ userInfo.mobile }}</view>
        </view>
        <view class="bahar-hero__qr bahar-press" @click="toMemberCode(userInfo.id ? userInfo.id : 0)">
          <text class="iconfont icon-qr-extract"></text>
        </view>
      </view>
      <!-- 余额 / 积分 统计区 -->
      <view class="bahar-hero__stats">
        <view class="bahar-hero__stat bahar-press" @click="toMemberWallet(userInfo.id ? userInfo.id : 0)">
          <view class="bahar-hero__stat-num">{{ isLogin ? userInfo.balance.toFixed(2) : '0.00' }}</view>
          <view class="bahar-hero__stat-label">余额(元)</view>
        </view>
        <view class="bahar-hero__stat bahar-press" @click="onTargetPoints()">
          <view class="bahar-hero__stat-num">{{ userInfo.point ? userInfo.point : 0 }}</view>
          <view class="bahar-hero__stat-label">积分</view>
        </view>
      </view>
      <view class="bahar-hero__bottom">
        <text class="bahar-hero__no" v-if="userInfo.userNo">会员号：{{ userInfo.userNo }}</text>
        <text class="bahar-hero__no" v-else></text>
        <view class="bahar-hero__recharge bahar-press" @click="toRecharge(userInfo.id ? userInfo.id : 0)">储值有礼 ›</view>
      </view>
    </view>

    <!--会员升级 start-->
    <view class="member-update bahar-card bahar-anim bahar-d2" v-if="memberGrade.length > 0">
        <view class="update-title">
            <text>会员升级</text>
        </view>
        <scroll-view scroll-x>
            <view class="recharge">
                <view class="recharge-item" :class="current == index ? 'recharge-item-active': ''" v-for="(item, index) in memberGrade" :key="index" :style="{marginLeft: !index ? '30rpx': ''}" @click="onShowPopup(index)">
                    <view class="recharge-tag">
                        <text class="recharge-tag-text" v-if="parseInt(item.validDay) > 0">{{ item.validDay }}天有效期</text>
                        <text class="recharge-tag-text" v-else>永久有效期</text>
                    </view>
                    <text class="recharge-item-duration">{{ item.name }}</text>
                    <view class="recharge-item-price">
                        <text class="rmb">￥</text>
                        <text class="recharge-item-price-text">{{ item.catchValue }}</text>
                    </view>
                    <text class="recharge-item-des" v-if="item.discount > 0">买单{{ item.discount }}折</text>
                    <text class="recharge-item-des" v-if="item.speedPoint > 0">积分翻{{ item.speedPoint }}倍</text>
                </view>
            </view>
        </scroll-view>
    </view>
    <!-- 弹窗 -->
    <Popup v-if="!isLoading" v-model="showPopup" @onPaySuccess="getPageData" :memberGrade="curGrade"/>
    <!--会员升级 end-->
    
    <!-- 订单操作 -->
    <view class="order-navbar bahar-card bahar-anim bahar-d3">
      <view class="nav-title">我的订单</view>
      <view class="nav-content">
          <view class="order-navbar-item" v-for="(item, index) in orderNavbar" :key="index" @click="onTargetOrder(item)">
            <view class="item-icon">
              <text class="iconfont" :class="[`icon-${item.icon}`]"></text>
            </view>
            <view class="item-name">{{ item.name }}</view>
            <text class="order-badge" v-if="item.count && item.count > 0">{{ item.count }}</text>
          </view>
      </view>
    </view>
    
    <!-- 服务单操作 -->
    <view class="order-navbar bahar-card bahar-anim bahar-d3">
      <view class="nav-title">服务单</view>
      <view class="nav-content">
          <view class="order-navbar-item" v-for="(item, index) in vehicleOrderNavbar" :key="index" @click="onTargetVehicleOrder(item)">
            <view class="item-icon">
              <text class="iconfont" :class="[`icon-${item.icon}`]"></text>
            </view>
            <view class="item-name">{{ item.name }}</view>
            <text class="order-badge" v-if="item.count && item.count > 0">{{ item.count }}</text>
          </view>
      </view>
    </view>

    <!-- 我的资产（asset-card 规格：渐变白卡 + 主色 6% 描边 + 64rpx 图标 + 2rpx 格间分隔线） -->
    <view class="my-asset bahar-card bahar-anim bahar-d4">
      <view class="asset-left flex-box dis-flex flex-x-center">
        <view class="asset-left-item" @click="onTargetMyCoupon('C')">
          <view class="item-icon">
            <text class="iconfont icon-youhuiquan"></text>
          </view>
          <view class="item-value dis-flex flex-x-center">
            <text>{{ isLogin ? assets.coupon : '0' }}</text>
          </view>
          <view class="item-name dis-flex flex-x-center">
            <text>优惠券</text>
          </view>
        </view>
        <view class="asset-left-item" @click="onTargetMyCoupon('P')">
          <view class="item-icon">
            <text class="iconfont icon-qianbao"></text>
          </view>
          <view class="item-value dis-flex flex-x-center">
            <text>{{ isLogin ? assets.prestore : '0' }}</text>
          </view>
          <view class="item-name dis-flex flex-x-center">
            <text>储值卡</text>
          </view>
        </view>
        <view class="asset-left-item" @click="onTargetMyCoupon('T')">
          <view class="item-icon">
            <text class="iconfont icon-zhangben"></text>
          </view>
          <view class="item-value dis-flex flex-x-center">
            <text>{{ isLogin ? assets.timer : '0' }}</text>
          </view>
          <view class="item-name dis-flex flex-x-center">
            <text>计次卡</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 我的服务 -->
    <view class="my-service bahar-card bahar-anim bahar-d5">
      <view class="service-title">我的服务</view>
      <view class="service-content clearfix">
        <block v-for="(item, index) in service" :key="index">
          <view v-if="item.type == 'link'" class="service-item" @click="handleService(item)">
            <view class="item-icon">
              <text class="iconfont" :class="[`icon-${item.icon}`]"></text>
            </view>
            <view class="item-name">{{ item.name }}</view>
          </view>
          <view v-if="item.type == 'button' && $platform == 'MP-WEIXIN'" class="service-item">
            <button class="btn-normal" :open-type="item.openType">
              <view class="item-icon">
                <text class="iconfont" :class="[`icon-${item.icon}`]"></text>
              </view>
              <view class="item-name">{{ item.name }}</view>
            </button>
          </view>
        </block>
        <block>
          <view v-if="isMerchant == true" class="service-item" @click="handleService({'url': 'merchantPages/index'})">
            <view class="item-icon">
              <text class="iconfont icon-dianpu"></text>
            </view>
            <view class="item-name">商户管理</view>
          </view>
          <view v-else class="service-item disabled" @click="handleBeMerchant()">
              <view class="item-icon">
                <text class="iconfont icon-dianpu"></text>
              </view>
              <view class="item-name">商户管理</view>
          </view>
        </block>
      </view>
    </view>
    
    <view class="my-recommend"></view>
  </view>
</template>

<script>
  import SettingKeyEnum from '@/common/enum/setting/Key'
  import SettingModel from '@/common/model/Setting'
  import * as UserApi from '@/api/user'
  import * as OrderApi from '@/api/order'
  import * as MessageApi from '@/api/message'
  import { checkLogin, showMessage } from '@/utils/app'
  import Popup from './components/Popup'

  // 订单操作
  const orderNavbar = [
    { id: 'all', name: '全部', icon: 'qpdingdan' },
    { id: 'toPay', name: '待支付', icon: 'daifukuan', count: 0 },
    { id: 'paid', name: '已支付', icon: 'daishouhuo', count: 0 }
  ]
  
  // 服务单操作
  const vehicleOrderNavbar = [
    { id: 'all', name: '全部', icon: 'qpdingdan' },
    { id: 'toPay', name: '服务中', icon: 'daifukuan', count: 0 },
    { id: 'paid', name: '已完成', icon: 'daishouhuo', count: 0 }
  ]

  /**
   * 我的服务
   * id: 标识; name: 标题名称; icon: 图标; type 类型(link和button); url: 跳转的链接
   */
  const service = [
    { id: 'myCoupon', name: '卡券兑换', icon: 'youhuiquan', type: 'link', url: 'subPages/coupon/receive' },
    { id: 'coupon', name: '转赠记录', icon: 'lingquan', type: 'link', url: 'pages/give/index' },
    { id: 'points', name: '我的积分', icon: 'jifen', type: 'link', url: 'pages/points/detail' },
    { id: 'book', name: '我的预约', icon: 'tuxingyanzhengma', type: 'link', url: 'subPages/book/my' },
    { id: 'help', name: '我的帮助', icon: 'bangzhu', type: 'link', url: 'pages/help/index' },
    { id: 'contact', name: '在线客服', icon: 'kefu', type: 'button', openType: 'contact' },
    { id: 'address', name: '收货地址', icon: 'shouhuodizhi', type: 'link', url: 'pages/address/index' },
    { id: 'refund', name: '售后服务', icon: 'shouhou', type: 'link', url: 'pages/refund/index' },
    { id: 'setting', name: '个人信息', icon: 'shezhi1', type: 'link', url: 'pages/user/setting' },
    { id: 'commission', name: '分佣提成', icon: 'zijinmingxi', type: 'link', url: 'subPages/commission/statistics' }
  ]

  export default {
    components: {
      Popup
    },
    data() {
      return {
        // 枚举类
        SettingKeyEnum,
        // 当前运行的终端 (此处并不冗余,因为微信小程序端view层无法直接读取$platform)
        $platform: this.$platform,
        // 正在加载
        isLoading: true,
        // 是否已登录
        isLogin: false,
        // 系统设置
        setting: {},
        // 当前用户信息
        userInfo: { id: 0, name: '', avatar: '', gradeId: 0, mobile: '', balance: 0 },
        gradeInfo: {},
        isMerchant: false,
        gradeEndTime: '',
        // 账户资产
        assets: { prestore: '0', timer: '0', coupon: '0' },
        // 我的服务
        service,
        // 订单操作
        orderNavbar,
        // 服务单操作
        vehicleOrderNavbar,
        // 当前用户待处理的订单数量
        todoCounts: { payment: 0 },
        current: 0,
        // 显示、隐藏弹窗
        showPopup: false,
        memberGrade: [],
        curGrade: {}
      }
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow(options) {
      // 获取页面数据
      this.getPageData()
      
      // 判断是否已登录
      this.isLogin = checkLogin()
      
      // 消息显示
      showMessage();
    },

    methods: {
      // 获取页面数据
      getPageData(callback) {
        const app = this
        app.isLoading = true
        Promise.all([app.getSetting(), app.getUserInfo(), app.getUserAssets(), app.getTodoCounts()])
          .then(result => {
            app.isLoading = false
            // 初始化我的服务数据
            app.initService()
            // 初始化订单操作数据
            app.initOrderTabbar()
            // 执行回调函数
            callback && callback()
          })
          .catch(err => {
            console.log('catch', err)
          })
      },

      // 初始化我的服务数据
      initService() {
        const app = this
        const newService = []
        service.forEach(item => {
          if (item.id === 'points') {
              item.name = '我的积分'
          }
          newService.push(item)
        })
        app.service = newService
      },

      // 初始化订单操作数据
      initOrderTabbar() {
        const app = this
        const newOrderNavbar = []
        orderNavbar.forEach(item => {
          if (item.hasOwnProperty('count')) {
              item.count = app.isLogin ? app.todoCounts[item.id] : 0
          }
          newOrderNavbar.push(item)
        })
        app.orderNavbar = newOrderNavbar;
        app.vehicleOrderNavbar = vehicleOrderNavbar;
      },

      // 获取设置
      getSetting() {
        const app = this
        app.setting = {}
      },

      // 获取当前用户信息
      getUserInfo() {
        const app = this
        app.showPopup = false;
        return new Promise((resolve, reject) => {
            UserApi.info()
            .then(result => {
              if (result.data.userInfo) {
                  app.userInfo = result.data.userInfo
                  app.isLogin = true
              } else {
                  app.isLogin = false
                  app.userInfo = { id: 0, name: '', avatar: '', gradeId: 0, mobile: '', balance: 0 }
              }
              // 强制更新头像或昵称
              if (result.data.needUpdateAvatar || result.data.needUpdateNickname) {
                  let tips = [];
                  if (result.data.needUpdateAvatar) tips.push('头像');
                  if (result.data.needUpdateNickname) tips.push('昵称');
                  uni.showModal({
                      title: '提示',
                      content: '请先完善您的' + tips.join('和'),
                      showCancel: false,
                      confirmText: '去完善',
                      success: () => {
                          app.$navTo('pages/user/setting')
                      }
                  });
              }
              app.gradeInfo = result.data.gradeInfo
              app.memberGrade = result.data.memberGrade
              app.gradeEndTime = result.data.gradeEndTime
              app.isMerchant = result.data.isMerchant
              
              resolve(app.userInfo)
              resolve(app.gradeInfo)
              resolve(isMerchant)
            })
            .catch(err => {
              if (err.result && err.result.status == 1001) {
                app.isLogin = false
                resolve(null)
              } else {
                reject(err)
              }
            })
        })
      },

      // 获取账户资产
      getUserAssets() {
        const app = this
        return new Promise((resolve, reject) => {
            UserApi.assets()
            .then(result => {
              app.assets = result.data.asset
              resolve(app.assets)
            })
            .catch(err => {
              if (err.result && err.result.status == 1001) {
                app.isLogin = false
                resolve(null)
              } else {
                reject(err)
              }
            })
        })
      },

      // 获取当前用户待处理的事项数量
      getTodoCounts() {
        const app = this
        return new Promise((resolve, reject) => {
          !app.isLogin ? resolve(null) : OrderApi.todoCounts()
            .then(result => {
              app.todoCounts = result.data
              resolve(app.todoCounts)
            })
        })
      },
      
      // 会员等级
      onShowPopup(index) {
        this.showPopup = !this.showPopup
        this.current = index
        this.curGrade = this.memberGrade[index]
      },
      
      // 跳转到会员码
      toMemberCode(userId) {
          !this.isLogin && this.$navTo('pages/login/index')
          this.$navTo('pages/user/code', { userId: userId})
      },
      
      // 跳转我的余额
      toMemberWallet(userId) {
          !this.isLogin && this.$navTo('pages/login/index')
          this.$navTo('pages/wallet/index', { userId: userId})
      },
      
      // 跳转充值
      toRecharge(userId) {
          !this.isLogin && this.$navTo('pages/login/index')
          this.$navTo('pages/wallet/recharge/index', { userId: userId})
      },

      // 跳转到订单页
      onTargetOrder(item) {
          !this.isLogin && this.$navTo('pages/login/index')
          this.$navTo('pages/order/index', { dataType: item.id })
      },
      
      // 跳转到服务单页
      onTargetVehicleOrder(item) {
          !this.isLogin && this.$navTo('pages/login/index')
          this.$navTo('pages/vehicleOrder/index', { dataType: item.id })
      },

      // 跳转到我的积分页面
      onTargetPoints() {
         !this.isLogin && this.$navTo('pages/login/index')
         this.$navTo('pages/points/detail')
      },

      // 跳转到我的卡券列表页
      onTargetMyCoupon(type) {
          const app = this
          if (app.isLogin) {
              // #ifdef MP-WEIXIN
              MessageApi.getSubTemplate({keys: "couponExpire,couponArrival"}).then(result => {
                  const templateIds = result.data
                  wx.requestSubscribeMessage({tmplIds: templateIds, 
                  success(res) {
                      console.log("调用成功！")
                  }, fail(res) {
                      console.log("调用失败:", res)
                  }, complete() {
                      app.$navTo('pages/my-coupon/index?type='+type)
                  }})
              })
              // #endif
              // #ifndef MP-WEIXIN
                 app.$navTo('pages/my-coupon/index?type='+type)
              // #endif
          } else {
              app.$navTo('pages/login/index')
          }
      },
      
      // 跳转会员设置页面
      onUserInfo() {
          if (!this.isLogin) {
              this.$navTo('pages/login/index')
          } else {
              this.$navTo('pages/user/setting')
          }
      },

      // 跳转到服务页面
      handleService({ url }) {
          this.$navTo(url)
      },
      // 成为商家
      handleBeMerchant() {
          this.$error('请联系管理员添加您的员工身份信息！');
      }
    },

    /**
     * 下拉刷新
     */
    onPullDownRefresh() {
      // 获取首页数据
      this.getPageData(() => {
        uni.stopPullDownRefresh()
      })
    }
  }
</script>

<style lang="scss" scoped>
  // 我的资产（竞品 asset-card 规格：渐变白卡 / 16rpx 圆角 / 主色 6% 描边+阴影 /
  // 图标 64rpx / 数值 42rpx / 标签 22rpx / 格子间 2rpx 分隔线）
  .my-asset {
    display: flex;
    background: linear-gradient(to bottom, #ffffff 0%, #fbfcfc 100%);
    margin: 0rpx 20rpx 0rpx 20rpx;
    padding: 32rpx 0 28rpx;
    border: 2rpx solid rgba($bahar-theme, 0.06);
    border-radius: 16rpx;
    box-shadow: 0 4rpx 16rpx rgba($bahar-theme, 0.06);
    .asset-right {
      width: 200rpx;
      border-left: 1rpx solid #eee;
    }
    .asset-left-item {
      position: relative;
      text-align: center;
      color: #666;
      padding: 0 20rpx;
      width: 33.33%;

      /* 格子之间的 2rpx 分隔线（最后一格右侧不画） */
      &:not(:last-child)::after {
        content: '';
        position: absolute;
        top: 18%;
        right: 0;
        width: 2rpx;
        height: 64%;
        background: rgba($bahar-theme, 0.08);
      }

      .item-icon {
        width: 64rpx;
        height: 64rpx;
        margin: 0 auto 12rpx;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 32rpx;
        color: #fff;
        background: linear-gradient(135deg, $bahar-theme, rgba($bahar-theme, 0.72));
      }

      .item-value {
        font-size: 42rpx;
        /* 资产数字与商品价同色（红色只做数字强调） */
        color: #fa5151;
        font-weight: bold;
      }

      .item-name {
        font-size: 22rpx;
        margin-top: 6rpx;
        color: #888;
      }
    }

  }

  // 订单操作
  .order-navbar {
    margin: 10rpx auto 10rpx auto;
    padding: 20rpx 0;
    width: 94%;
    box-shadow: 0 1rpx 5rpx 0px rgba(0, 0, 0, 0.05);
    font-size: 30rpx;
    border-radius: 10rpx;
    background: #fff;
    border: 2rpx #f5f5f5 solid;
    .nav-title {
        clear: both;
        display: block;
        width: 100%;
        height: 60rpx;
        line-height: 60rpx;
        padding-left: 20rpx;
        margin-bottom: 20rpx;
        font-size: 24rpx;
    }
    .nav-content {
        display: flex;
        .order-navbar-item {
          position: relative;
          width: 33%;
          .item-icon {
            width: 80rpx;
            height: 80rpx;
            margin: 0 auto 10rpx;
            border-radius: 22rpx;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 40rpx;
          }
          &:nth-child(1) .item-icon { background: linear-gradient(135deg, $bahar-theme, lighten($bahar-theme, 10%)); }
          &:nth-child(2) .item-icon { background: linear-gradient(135deg, #ff9500, #ffb340); }
          &:nth-child(3) .item-icon { background: linear-gradient(135deg, #5b7cff, #86a0ff); }

          .item-name {
            font-size: 24rpx;
            color: #545454;
            text-align: center;
            margin-right: 10rpx;
          }

          .order-badge {
            position: absolute;
            top: 0;
            right: 58rpx;
            font-size: 20rpx;
            background: #fa5151;
            text-align: center;
            line-height: 30rpx;
            color: #fff;
            border-radius: 50%;
            min-width: 36rpx;
            padding: 6rpx 13rpx 6rpx 13rpx;
          }
        }
    }
  }

  // 我的服务
  .my-service {
    margin: 0rpx auto 20rpx auto;
    border: 2rpx #f5f5f5 solid;
    background: #FFF;
    padding: 10rpx 0rpx;
    width: 94%;
    box-shadow: 0 1rpx 5rpx 0px rgba(0, 0, 0, 0.05);
    border-radius: 10rpx;
    display: block;

    .service-title {
      padding-left: 20rpx;
      margin-bottom: 30rpx;
      font-size: 28rpx;
    }

    .service-content {
      $chip-bg: (#e8f7f7, #fff3e5, #eef2ff, #fdeeee, #edf9f0, #f6ecff);
      $chip-fg: ($bahar-theme, #ff9500, #5b7cff, #f56c6c, #2fbf67, #9b5cf6);
      .service-item {
        width: 25%;
        float: left;
        margin-bottom: 25rpx;

        .item-icon {
          width: 72rpx;
          height: 72rpx;
          margin: 0 auto 12rpx;
          border-radius: 22rpx;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 38rpx;
        }

        .item-name {
          font-size: 24rpx;
          color: #545454;
          text-align: center;
          margin-right: 10rpx;
        }
      }
      @for $i from 1 through 12 {
        $idx: ($i - 1) % 6 + 1;
        .service-item:nth-child(#{$i}) .item-icon {
          background: nth($chip-bg, $idx);
          color: nth($chip-fg, $idx);
        }
      }
      .disabled {
         .item-icon {
           color: #cccccc;
         }
         .item-name {
            color: #cccccc;
         }
      }
    }
  }
  
  // 推荐信息
  .my-recommend {
      height: 20rpx;
  }
  
  // 会员升级
  .member-update {
      margin: 22rpx auto 22rpx auto;
      padding: 20rpx 0;
      border-radius: 10rpx;
      box-shadow: 0 1rpx 5rpx 0px rgba(0, 0, 0, 0.05);
      background: #fff;
      width: 94%;
      text-align: center;
      .update-title {
        padding-left: 20rpx;
        margin-bottom: 30rpx;
        font-size: 28rpx;
        text-align: left;
      }
      .recharge {
            position: relative;
            margin-bottom: 35rpx;
            display: flex;
            flex-direction: row;
            align-items: center;
            
            &-tag {
                position: absolute;
                top: -2rpx;
                left: -2rpx;
                width: 170rpx;
                height: 36rpx;
                display: flex;
                flex-direction: row;
                align-items: center;
                justify-content: center;
                background-image: url('~@/static/user/tag.png');
                background-size: 100%;
                &-text {
                    font-size: 20rpx;
                    color: #FFFFFF;
                    text-align: center;
                }
            }
            
            &-item {
                position: relative;
                padding: 40rpx 0;
                margin-left: 15rpx;
                width: 29.33%;
                height: 270rpx;
                flex-shrink: 0;
                display: flex;
                flex-direction: column;
                align-items: center;
                border: solid 1rpx #CBCCCE;
                border-radius: 12rpx;
                
                &-active {
                    border: solid 2rpx #EDD2A9;
                    background-color: #FBF1E5;
                }
                
                &-duration {
                    margin-bottom: 30rpx;
                    font-size: 26rpx;
                    color: #1C1C1C;
                }
                
                &-price {
                    margin-bottom: 20rpx;
                    display: flex;
                    flex-direction: row;
                    align-items: baseline;
                    
                    &-text {
                        font-size: 48rpx;
                        color: #E3BE83;
                    }
                }
                
                &-des {
                    font-size: 22rpx;
                    color: #A5A3A2;
                }
            }
        }
    }
</style>
