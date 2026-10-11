<template>
    <!-- 汽车首页四宫格。结构/数值与 components/page/navBar 对齐，
         保证四套会员端首页的四宫格是同一形态（图标 + 主标题 + 副标题）。 -->
    <view class="navigation bahar-card bahar-anim">
        <view class="nav-rows" :class="[`nav-rows--${rowsNum}`]">
            <view class="item-nav" v-for="(item, index) in navigation" :key="index">
                <view class="nav-to bahar-press" @click.stop="goUrl(item.url)">
                    <view class="item-image">
                        <image class="icon" mode="aspectFit" :src="item.iconUrl"></image>
                    </view>
                    <view class="item-text">
                        <view class="text">{{ item.name }}</view>
                        <view class="tip">{{ item.tips ? item.tips : '' }}</view>
                    </view>
                </view>
            </view>
        </view>
    </view>
</template>

<script>
export default {
    props: {
        navigation: {
            type: Array,
            default: () => []
        },
        /**
         * 每行列数。默认 4 列，一行放不下时自动换行。
         */
        rowsNum: {
            type: [Number, String],
            default: 4
        }
    },
    methods: {
        goUrl(url) {
            this.$navTo(url);
        }
    }
}
</script>

<style lang="scss" scoped>
/* 外框：覆盖 .bahar-card 的 24rpx 内边距，改为 6rpx，
   与 components/page/navBar 的内卡尺寸一致（外卡边距仍是 24rpx）。 */
.navigation {
    padding: 6rpx;
    border-radius: 20rpx;
    border: 1rpx solid #e6e6e6;
    box-sizing: border-box;

    .nav-rows {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        justify-content: center;

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

            .item-image {
                margin: 0 auto;
                font-size: 0;
                width: 72rpx;
                height: 72rpx;
                box-sizing: border-box;

                .icon {
                    width: 72rpx;
                    height: 72rpx;
                    border-radius: 16rpx;
                }
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
        }
    }
}

/* 分列：等分列宽，超出自动换行（不再裁掉第二行） */
.nav-rows--4 > .item-nav { flex: 0 0 25%; }
.nav-rows--3 > .item-nav { flex: 0 0 33.33333333%; }
.nav-rows--2 > .item-nav { flex: 0 0 50%; }
</style>
