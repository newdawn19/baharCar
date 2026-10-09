<script>
  export default {

    /**
     * 全局变量
     */
    globalData: {

    },

    /**
     * 初始化完成时触发
     */
    onLaunch(options) {
      // 小程序主动更新
      this.updateManager()
      if (options.query.spm) {
          uni.setStorageSync('shareId', options.query.spm);
      }
    },

    methods: {

      /**
       * 小程序主动更新
       */
      updateManager() {
        const updateManager = uni.getUpdateManager();
        updateManager.onCheckForUpdate(res => {
          // 请求完新版本信息的回调
          // console.log(res.hasUpdate)
        })
        updateManager.onUpdateReady(() => {
          uni.showModal({
            title: '更新提示',
            content: '新版本已经准备好，即将重启应用',
            showCancel: false,
            success(res) {
              if (res.confirm) {
                // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
                updateManager.applyUpdate()
              }
            }
          })
        })
        updateManager.onUpdateFailed(() => {
          // 新的版本下载失败
          uni.showModal({
            title: '更新提示',
            content: '新版本下载失败',
            showCancel: false
          })
        })
      }
    }

  }
</script>

<style lang="scss">
  /* 引入uView库样式 */
  @import "uview-ui/index.scss";

  /* bahar 统一卡片/间距工具类（全局） */
  .bahar-page { background-color: #f7f8fa; min-height: 100vh; }
  .bahar-card { background: #fff; border-radius: 16rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); padding: 24rpx; margin: 24rpx; }
  .bahar-card--flat { box-shadow: none; }
  .bahar-section-title { font-size: 30rpx; font-weight: 600; color: #333; padding: 24rpx 24rpx 12rpx; }
  .bahar-mt { margin-top: 24rpx; }
  .bahar-mb { margin-bottom: 24rpx; }

  /* 品牌渐变头图（装饰圆版：列表页通用） */
  .bahar-gradient-header {
    position: relative;
    overflow: hidden;
    background-image: linear-gradient(135deg, $bahar-theme, lighten($bahar-theme, 14%));
    color: #fff;
    padding: 36rpx 30rpx 64rpx;
    &::before, &::after {
      content: '';
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.10);
    }
    &::before { width: 280rpx; height: 280rpx; top: -140rpx; right: -70rpx; }
    &::after { width: 180rpx; height: 180rpx; bottom: -90rpx; left: -50rpx; background: rgba(255, 255, 255, 0.07); }
  }

  /* ===== 会员 Hero 卡（会员中心主视觉） ===== */
  .bahar-hero {
    position: relative;
    overflow: hidden;
    margin: 24rpx 24rpx 0;
    padding: 36rpx 32rpx 40rpx;
    /* 竞品规格：会员卡高 280rpx。用 min-height 而不是 height —— 卡内还有
       头像/等级/余额积分/会员号四层内容，写死 height 在字号放大或文案换行时会把内容挤出去。 */
    min-height: 280rpx;
    border-radius: 24rpx;
    background-image: linear-gradient(135deg, darken($bahar-theme, 8%) 0%, $bahar-theme 55%, lighten($bahar-theme, 16%) 100%);
    color: #fff;
    box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.12);
    &::before, &::after {
      content: '';
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.10);
    }
    &::before { width: 320rpx; height: 320rpx; top: -160rpx; right: -90rpx; }
    &::after { width: 200rpx; height: 200rpx; bottom: -60rpx; left: -70rpx; background: rgba(255, 255, 255, 0.08); }
  }
  .bahar-hero__top { position: relative; display: flex; align-items: center; }
  .bahar-hero__avatar {
    width: 110rpx; height: 110rpx; border-radius: 50%;
    border: 4rpx solid rgba(255, 255, 255, 0.65);
    background: rgba(255, 255, 255, 0.25);
    padding: 4rpx; flex-shrink: 0; box-sizing: border-box;
    .image { display: block; width: 100%; height: 100%; border-radius: 50%; }
  }
  .bahar-hero__user { flex: 1; min-width: 0; margin-left: 24rpx; }
  .bahar-hero__name { font-size: 36rpx; font-weight: 700; max-width: 340rpx; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
  .bahar-hero__sub { font-size: 24rpx; opacity: 0.85; margin-top: 8rpx; }
  .bahar-hero__grade {
    display: inline-flex; align-items: center; margin-top: 10rpx;
    padding: 4rpx 20rpx; border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.22);
    border: 1rpx solid rgba(255, 255, 255, 0.35);
    font-size: 22rpx; color: #f7e6c3;
  }
  .bahar-hero__qr {
    width: 76rpx; height: 76rpx; border-radius: 50%; flex-shrink: 0;
    background: rgba(255, 255, 255, 0.20);
    border: 1rpx solid rgba(255, 255, 255, 0.30);
    display: flex; align-items: center; justify-content: center;
    font-size: 40rpx;
  }
  .bahar-hero__bottom {
    position: relative; display: flex; align-items: center;
    justify-content: space-between; margin-top: 26rpx; font-size: 24rpx;
  }
  .bahar-hero__recharge {
    background: #fff; color: $bahar-theme; font-weight: 600;
    padding: 8rpx 26rpx; border-radius: 999rpx; font-size: 24rpx;
  }
  /* 会员卡内 余额/积分 统计区（两列，中间细分割线） */
  .bahar-hero__stats {
    position: relative;
    display: flex;
    justify-content: flex-end;
    align-items: flex-start;
    margin-top: 26rpx;
  }
  .bahar-hero__stat {
    min-width: 170rpx;
    padding: 0 20rpx;
    text-align: right;
  }
  .bahar-hero__stat + .bahar-hero__stat {
    border-left: 1rpx solid rgba(255, 255, 255, 0.28);
  }
  .bahar-hero__stat-num { font-size: 36rpx; font-weight: 700; color: #fff; line-height: 1.2; }
  .bahar-hero__stat-label { font-size: 22rpx; color: rgba(255, 255, 255, 0.85); margin-top: 6rpx; }

  /* ===== 动效系统 ===== */
  @keyframes bahar-rise {
    from { opacity: 0; transform: translateY(28rpx); }
    to { opacity: 1; transform: translateY(0); }
  }
  .bahar-anim { animation: bahar-rise 0.5s cubic-bezier(0.22, 0.89, 0.35, 1.03) both; }
  .bahar-d1 { animation-delay: 0.06s; }
  .bahar-d2 { animation-delay: 0.12s; }
  .bahar-d3 { animation-delay: 0.18s; }
  .bahar-d4 { animation-delay: 0.24s; }
  .bahar-d5 { animation-delay: 0.30s; }

  /* 按压反馈 */
  .bahar-press { transition: transform 0.15s ease, box-shadow 0.15s ease; }
  .bahar-press:active { transform: scale(0.97); }

  /* 彩色软底图标 chip（服务宫格 / 订单入口） */
  .bahar-chip {
    width: 72rpx; height: 72rpx; border-radius: 22rpx;
    display: flex; align-items: center; justify-content: center;
  }
  .bahar-chip--1 { background: #e8f7f7; color: $bahar-theme; }
  .bahar-chip--2 { background: #fff3e5; color: #ff9500; }
  .bahar-chip--3 { background: #eef2ff; color: #5b7cff; }
  .bahar-chip--4 { background: #fdeeee; color: #f56c6c; }
  .bahar-chip--5 { background: #edf9f0; color: #2fbf67; }
  .bahar-chip--6 { background: #f6ecff; color: #9b5cf6; }

  /* 品牌化 u-empty 空状态 */
  .u-empty .u-icon__icon,
  .u-empty .u-icon__label { color: $bahar-theme !important; }
</style>

<style>
  /* 项目基础样式 */
  @import "./app.scss";
</style>
