<template>
    <view class="navigation bahar-card bahar-anim">
        <view class="nav">
            <view class="nav-rows" :class="[`nav-rows--${rowsNum}`]">
                <view class="item bahar-press" v-for="item in navigation">
                    <image class="icon" mode="aspectFit" :src="item.iconUrl" @click.stop="goUrl(item.url)"></image>
                    <view class="title">{{ item.name }}</view>
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
            default: []
        },
        /**
         * 每行列数。
         * 原来写死 3 列 + 容器固定高 380rpx：导航超过 3 个时第二行会被裁掉，
         * 且内层 10rpx 比全局 .bahar-card 的 24rpx 小一截，和下方卡片左右对不齐。
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
.nav {
    /* 外边距由 .bahar-card 统一给 24rpx，这里不能再叠一层，
       否则四宫格会比下方商品卡/优惠券卡窄 14rpx。 */
    margin: 0;
    padding: 12rpx 0;
    border-radius: 10rpx;
    background-color: #ffffff;
    /* 高度随行数自适应，不再写死 380rpx */
    height: auto;
    align-items: center;
    justify-content: center;
    .nav-rows {
        display: flex;
        flex-wrap: wrap;
        .item {
            text-align: center;
            justify-content: center;
            align-items: center;
            display: block;
            float: left;
            background: #fff;
            margin-bottom: 16rpx;
            .icon {
                width: 72rpx;
                height: 72rpx;
                margin: 12rpx auto 8rpx;
                padding: 8rpx;
                box-sizing: content-box;
                border: none;
                border-radius: 24rpx;
                background: rgba($bahar-theme, 0.08);
            }
            .title {
                font-size: 24rpx;
                color: #333;
                font-weight: 600;
                padding: 0 4rpx;
                overflow: hidden;
                white-space: nowrap;
                text-overflow: ellipsis;
            }
        }
    }
}

/* 分列：等分列宽，超出自动换行（不再裁掉第二行） */
.nav-rows--4 > .item { width: 25%; }
.nav-rows--3 > .item { width: 33.33333333%; }
.nav-rows--2 > .item { width: 50%; }
</style>
