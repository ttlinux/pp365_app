package org.sex.hanker.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import org.sex.hanker.View.MyLinearLayout;
import org.sex.hanker.View.MyRadioButton;
import org.sex.hanker.View.MyRelativeLayout;
import org.sex.hanker.mybusiness.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageDownLoader {
    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<String, Bitmap> mMemoryCache;
    /**
     * 操作文件相关类对象的引用
     */
    private FileUtils fileUtils;
    /**
     * 下载Image的线程池
     */
    private ExecutorService mImageThreadPool = null;
    private Bitmap bitmapd = null;
    private String furl;
    public static final int SRC=1;
    public static final int BackGround=0;
    public static final int CircleSRC=-9999;
    public static final int CircleBackGround=-1;


    public ImageDownLoader(Context context) {
        //获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        //给LruCache分配1/8 4M
        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {

            //必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

        };

        fileUtils = new FileUtils(context);
    }


    /**
     * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
     *
     * @return
     */
    public ExecutorService getThreadPool() {
        if (mImageThreadPool == null) {
            synchronized (ExecutorService.class) {
                if (mImageThreadPool == null) {
                    //为了下载图片更加的流畅，我们用了2个线程来下载图片
                    mImageThreadPool = Executors.newFixedThreadPool(3);
                }
            }
        }

        return mImageThreadPool;

    }

    /**
     * 添加Bitmap到内存缓存
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从内存缓存中获取一个Bitmap
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 先从内存缓存中获取Bitmap,如果没有就从SD卡或者手机缓存中获取，SD卡或者手机缓存
     * 没有就去下载
     *
     * @param url
     * @param listener
     * @return
     */
    public Bitmap downloadImage(final String url, final ImageView imageView) {
        //替换Url中非字母和非数字的字符，这里比较重要，因为我们用Url作为文件名，比如我们的Url
        //是Http://xiaanming/abc.jpg;用这个作为图片名称，系统会认为xiaanming为一个目录，
        //我们没有创建此目录保存文件就会报错
        String handlerURL = url.contains("http://") ? "" : "http://";
        handlerURL = handlerURL + url;
        final String subUrl = handlerURL;
        Bitmap bitmap = showCacheBitmap(subUrl);
        if (bitmap != null) {
            return bitmap;
        } else {

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    imageView.setImageBitmap(getCircleBitmap((Bitmap) msg.obj));
//					listener.onImageLoader((Bitmap)msg.obj,imageView, url);
                }
            };

            getThreadPool().execute(new Runnable() {

                @Override
                public void run() {
                    Bitmap bitmap = getBitmapFormUrld(url);
                    if (bitmap != null) {
                        Message msg = handler.obtainMessage();
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }
                    try {
                        //保存在SD卡或者手机目录
                        fileUtils.savaBitmap(subUrl, bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //将Bitmap 加入内存缓存
                    addBitmapToMemoryCache(subUrl, bitmap);
                }
            });
        }

        return null;
    }

    /**
     * 获取Bitmap, 内存中没有就去手机或者sd卡中获取，这一步在getView中会调用，比较关键的一步
     *
     * @param url
     * @return
     */
    public Bitmap showCacheBitmap(String url) {
        if (getBitmapFromMemCache(url) != null) {
            return getBitmapFromMemCache(url);
        } else if (fileUtils.isFileExists(url) && fileUtils.getFileSize(url) != 0) {
            //从SD卡获取手机里面获取Bitmap
            Bitmap bitmap = fileUtils.getBitmap(url);

            //将Bitmap 加入内存缓存
            addBitmapToMemoryCache(url, bitmap);
            return bitmap;
        }

        return null;
    }


    /**
     * 从Url中获取Bitmap
     *
     * @param url
     * @return
     */
    private boolean getBitmapFormUrl(String url) {
//		HttpURLConnection con = null;
//		try {
//			URL mImageUrl = new URL(url);
//			con = (HttpURLConnection) mImageUrl.openConnection();
//			con.setConnectTimeout(15 * 1000);
//			con.setReadTimeout(15 * 1000);
//			con.setDoInput(true);
//			con.setDoOutput(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (con != null) {
//				con.disconnect();
//			}
//		}
        HttpURLConnection conn = null;
        URL mImageUrl = null;
        try {
            mImageUrl = new URL(url);
            conn = (HttpURLConnection) mImageUrl.openConnection();
            conn.setConnectTimeout(15 * 1000);
            conn.setReadTimeout(15 * 1000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = conn.getInputStream();
                if (in != null) {
                    try {
                        bitmapd = BitmapFactory.decodeStream(conn.getInputStream());
                        return true;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    in.close();
                    conn.disconnect();
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Bitmap getBitmapFormUrld(String url) {
        Bitmap bitmap = null;
        HttpURLConnection con = null;
        try {
            LogTools.e("urlurlurlurl", url);
            URL mImageUrl = new URL(url);
            con = (HttpURLConnection) mImageUrl.openConnection();
            con.setConnectTimeout(15 * 1000);
            con.setReadTimeout(15 * 1000);
//			con.setDoInput(true);
//			con.setDoOutput(true);
            con.setRequestMethod("GET");
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//		finally {
//			if (con != null) {
//				con.disconnect();
//			}
//		}
        return bitmap;
    }

    /**
     * 取消正在下载的任务
     */
    public synchronized void cancelTask() {
        if (mImageThreadPool != null) {
            mImageThreadPool.shutdownNow();
            mImageThreadPool = null;
        }
    }

    private Handler urlhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 更新UI，显示图片
            if (bitmapd != null) {
//	                mImageView.setImageBitmap(mBitmap);// display image
                bitmapd = (Bitmap) msg.obj;
            }
        }
    };

    @SuppressWarnings({"unused", "deprecation"})
    public void showImage_SetdefaultImage(final Context mContext, final String url, final ImageView img, final int i,final Drawable drawable) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        final String urlimg = url.replaceAll("[^\\w]", "");
        bitmap = showCacheBitmap(urlimg);
        if (bitmap != null) {
            if (i == 0) {
                img.setBackgroundDrawable(new BitmapDrawable(bitmap));
            } else if (i == -9999) {
                img.setImageBitmap(getCircleBitmap(bitmap));
            } else if (i == -1) {
                img.setBackgroundDrawable(new BitmapDrawable(getRoundedCornerBitmap(bitmap, 15, CORNER_TOP)));
            } else {
                img.setImageBitmap(bitmap);
            }
        } else {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
//						listener.onImageLoader((Bitmap)msg.obj, urlimg);
                    if ((Bitmap) msg.obj != null) {
                        if (i == 0) {
                            img.setBackgroundDrawable(new BitmapDrawable((Bitmap) msg.obj));
                        } else if (i == -9999) {
                            img.setImageBitmap(getCircleBitmap((Bitmap) msg.obj));
                        } else if (i == -1) {
                            img.setBackgroundDrawable(new BitmapDrawable(getRoundedCornerBitmap((Bitmap) msg.obj, 15, CORNER_TOP)));
                        } else {
                            img.setImageBitmap((Bitmap) msg.obj);
                        }
                    } else {
                        if (i == 0) {
                            img.setBackgroundDrawable(drawable);
                        } else {
                            img.setImageDrawable(drawable);
                        }
                    }
                }
            };
            getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    if (getBitmapFormUrl(url)) {
                        Message msg = handler.obtainMessage();
                        msg.obj = bitmapd;
                        handler.sendMessage(msg);
                        try {
                            //保存在SD卡或者手机目录
                            fileUtils.savaBitmap(urlimg, bitmapd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //将Bitmap 加入内存缓存
                        addBitmapToMemoryCache(urlimg, bitmapd);
                    }

                }
            });
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    public void showImage(final Context mContext, final String url, final ImageView img, final int i) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        final String urlimg = url.replaceAll("[^\\w]", "");
        bitmap = showCacheBitmap(urlimg);
        if (bitmap != null) {
            if (i == 0) {
                img.setBackgroundDrawable(new BitmapDrawable(bitmap));
            } else if (i == -9999) {
                img.setImageBitmap(getCircleBitmap(bitmap));
            } else if (i == -1) {
                img.setBackgroundDrawable(new BitmapDrawable(getRoundedCornerBitmap(bitmap, 15, CORNER_TOP)));
            } else {
                img.setImageBitmap(bitmap);
            }
        } else {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
//						listener.onImageLoader((Bitmap)msg.obj, urlimg);
                    if ((Bitmap) msg.obj != null) {
                        if (i == 0) {
                            img.setBackgroundDrawable(new BitmapDrawable((Bitmap) msg.obj));
                        } else if (i == -9999) {
                            img.setImageBitmap(getCircleBitmap((Bitmap) msg.obj));
                        } else if (i == -1) {
                            img.setBackgroundDrawable(new BitmapDrawable(getRoundedCornerBitmap((Bitmap) msg.obj, 15, CORNER_TOP)));
                        } else {
                            img.setImageBitmap((Bitmap) msg.obj);
                        }
                    } else {
                        if (i == 0) {
                            img.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.defaultimage));
                        } else {
                            img.setImageBitmap((Bitmap) BitmapFactory.decodeResource(mContext.getResources(), R.drawable.defaultimage));
                        }
                    }
                }
            };
            getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    if (getBitmapFormUrl(url)) {
                        Message msg = handler.obtainMessage();
                        msg.obj = bitmapd;
                        handler.sendMessage(msg);
                        try {
                            //保存在SD卡或者手机目录
                            fileUtils.savaBitmap(urlimg, bitmapd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //将Bitmap 加入内存缓存
                        addBitmapToMemoryCache(urlimg, bitmapd);
                    }

                }
            });
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    public void showImageForlistview(final Context mContext, final String url, final ImageView img, final int i) {
        if (img != null && img.getTag() != null) return;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        final String urlimg = url.replaceAll("[^\\w]", "");
        bitmap = showCacheBitmap(urlimg);
        if (bitmap != null) {
            if (i == 0) {
                img.setBackgroundDrawable(new BitmapDrawable(bitmap));
            } else if (i == -9999) {
                img.setImageBitmap(getCircleBitmap(bitmap));
            } else if (i == -1) {
                img.setBackgroundDrawable(new BitmapDrawable(getRoundedCornerBitmap(bitmap, 15, CORNER_TOP)));
            } else {
                img.setImageBitmap(bitmap);
                img.setTag(true);
            }
        } else {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
//						listener.onImageLoader((Bitmap)msg.obj, urlimg);
                    if ((Bitmap) msg.obj != null) {
                        if (i == 0) {
                            img.setBackgroundDrawable(new BitmapDrawable((Bitmap) msg.obj));
                        } else if (i == -9999) {
                            img.setImageBitmap(getCircleBitmap((Bitmap) msg.obj));
                        } else if (i == -1) {
                            img.setBackgroundDrawable(new BitmapDrawable(getRoundedCornerBitmap((Bitmap) msg.obj, 15, CORNER_TOP)));
                        } else {
                            img.setImageBitmap((Bitmap) msg.obj);
                            img.setTag(true);
                        }
                    } else {
                        if (i == 0) {
                            img.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.defaultimage));
                        } else {
                            img.setImageBitmap((Bitmap) BitmapFactory.decodeResource(mContext.getResources(), R.drawable.defaultimage));
                        }
                    }
                }
            };
            getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    if (getBitmapFormUrl(url)) {
                        Message msg = handler.obtainMessage();
                        msg.obj = bitmapd;
                        handler.sendMessage(msg);
                        try {
                            //保存在SD卡或者手机目录
                            fileUtils.savaBitmap(urlimg, bitmapd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //将Bitmap 加入内存缓存
                        addBitmapToMemoryCache(urlimg, bitmapd);
                    }

                }
            });
        }
    }



    @SuppressWarnings({"unused", "deprecation"})
    public void SetSpecialView(final Context mContext, final String url, final View img) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        final String urlimg = url.replaceAll("[^\\w]", "");
        bitmap = showCacheBitmap(urlimg);
        if (bitmap != null) {
            if (img instanceof MyLinearLayout)
                ((MyLinearLayout) img).setImage(bitmap);
            if (img instanceof MyRelativeLayout)
                ((MyRelativeLayout) img).setImage(bitmap);
            if (img instanceof MyRadioButton)
                ((MyRadioButton) img).setImage(bitmap);
        } else {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
//						listener.onImageLoader((Bitmap)msg.obj, urlimg);
                    if ((Bitmap) msg.obj != null) {
                        if (img instanceof MyLinearLayout)
                            ((MyLinearLayout) img).setImage((Bitmap) msg.obj);
                        if (img instanceof MyRelativeLayout)
                            ((MyRelativeLayout) img).setImage((Bitmap) msg.obj);
                        if (img instanceof MyRadioButton)
                            ((MyRadioButton) img).setImage((Bitmap) msg.obj);
                    }
                }
            };
            getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    if (getBitmapFormUrl(url)) {
                        Message msg = handler.obtainMessage();
                        msg.obj = bitmapd;
                        handler.sendMessage(msg);
                        try {
                            //保存在SD卡或者手机目录
                            fileUtils.savaBitmap(urlimg, bitmapd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //将Bitmap 加入内存缓存
                        addBitmapToMemoryCache(urlimg, bitmapd);
                    }
                }
            });
        }
    }

    /**
     * @param bitmap src图片
     * @return
     */
    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        //在画布上绘制一个圆
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static final int CORNER_NONE = 0;
    public static final int CORNER_TOP_LEFT = 1;
    public static final int CORNER_TOP_RIGHT = 1 << 1;
    public static final int CORNER_BOTTOM_LEFT = 1 << 2;
    public static final int CORNER_BOTTOM_RIGHT = 1 << 3;
    public static final int CORNER_ALL = CORNER_TOP_LEFT | CORNER_TOP_RIGHT | CORNER_BOTTOM_LEFT | CORNER_BOTTOM_RIGHT;
    public static final int CORNER_TOP = CORNER_TOP_LEFT | CORNER_TOP_RIGHT;
    public static final int CORNER_BOTTOM = CORNER_BOTTOM_LEFT | CORNER_BOTTOM_RIGHT;
    public static final int CORNER_LEFT = CORNER_TOP_LEFT | CORNER_BOTTOM_LEFT;
    public static final int CORNER_RIGHT = CORNER_TOP_RIGHT | CORNER_BOTTOM_RIGHT;

    //获得圆角图片的方法
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPx, int corners) {
        try {
            // 其原理就是：先建立一个与图片大小相同的透明的Bitmap画板
            // 然后在画板上画出一个想要的形状的区域。
            // 最后把源图片帖上。
            final int width = bitmap.getWidth();
            final int height = bitmap.getHeight();

            Bitmap paintingBoard = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(paintingBoard);
            canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);

            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);

            //画出4个圆角
            final RectF rectF = new RectF(0, 0, width, height);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            //把不需要的圆角去掉
            int notRoundedCorners = corners ^ CORNER_ALL;
            if ((notRoundedCorners & CORNER_TOP_LEFT) != 0) {
                clipTopLeft(canvas, paint, roundPx, width, height);
            }
            if ((notRoundedCorners & CORNER_TOP_RIGHT) != 0) {
                clipTopRight(canvas, paint, roundPx, width, height);
            }
            if ((notRoundedCorners & CORNER_BOTTOM_LEFT) != 0) {
                clipBottomLeft(canvas, paint, roundPx, width, height);
            }
            if ((notRoundedCorners & CORNER_BOTTOM_RIGHT) != 0) {
                clipBottomRight(canvas, paint, roundPx, width, height);
            }
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            //帖子图
            final Rect src = new Rect(0, 0, width, height);
            final Rect dst = src;
            canvas.drawBitmap(bitmap, src, dst, paint);
            return paintingBoard;
        } catch (Exception exp) {
            return bitmap;
        }
    }

    private static void clipTopLeft(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(0, 0, offset, offset);
        canvas.drawRect(block, paint);
    }

    private static void clipTopRight(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(width - offset, 0, width, offset);
        canvas.drawRect(block, paint);
    }

    private static void clipBottomLeft(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(0, height - offset, offset, height);
        canvas.drawRect(block, paint);
    }

    private static void clipBottomRight(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(width - offset, height - offset, width, height);
        canvas.drawRect(block, paint);
    }

    /**
     * 异步下载图片的回调接口
     *
     * @author len
     */
    public interface onImageLoaderListener {
        void onImageLoader(Bitmap bitmap, String url);
    }

    public interface onImageLoaderListener2 {
        void onImageLoader(Bitmap bitmap, ImageView imageView, String url);
    }
}
