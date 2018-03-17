package org.sex.hanker.Utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by Administrator on 2018/3/17.
 */
public class GlideModuleConfig implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
//内部存储/Android/data/包名/cache/glide-images
        builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, BundleTag.PicSavePath, 2 * 1024 * 1024));
//将默认的RGB_565效果转换到ARGB_8888
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        //不做处理
    }
}
