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

  /* 品牌渐变头图 */
  .bahar-gradient-header {
    background-image: linear-gradient(135deg, $bahar-theme, lighten($bahar-theme, 12%));
    color: #fff;
    padding: 32rpx 24rpx;
  }

  /* 品牌化 u-empty 空状态 */
  .u-empty .u-icon__icon,
  .u-empty .u-icon__label { color: $bahar-theme !important; }
</style>

<style>
  /* 项目基础样式 */
  @import "./app.scss";
</style>
