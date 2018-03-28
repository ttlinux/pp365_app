package org.sex.hanker.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.sex.hanker.Bean.ChatBean;
import org.sex.hanker.mybusiness.R;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.LinkedList;


/**
 * Created by Administrator on 2018/2/1.
 */
public class ChatMethod extends BroadcastReceiver {
    private String tag="ChatMethod";
    public WebSocketClient client;
    public String[] args;
    private Context context;
    private static final int MessageSpeed=3000;
    private static final int CheckPeriod=30000;
//    private long lasttimeMessage=0;
    private LinkedList<String> linkedList=new LinkedList<>();
    ChatListener chatlistener;
    private static final int Startcheck=0;
    private long LasttimeChat;
    private static final String Heartbeat="@heart";
    private int networkstate_Record=2;
    private boolean Connectstate=false;

    public ChatMethod() {
        super();
    }

    public void setLasttimeChat(long lasttimeChat) {
        LasttimeChat = lasttimeChat;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    Handler handler=new Handler(){

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what)
            {
                case Startcheck:
                    if(System.currentTimeMillis()-LasttimeChat>=CheckPeriod)
                    {
//                        ChatMethod.this.sendMessage(Heartbeat);
                        if(client!=null && client.isOpen())
                        client.sendPing();
                    }
                    sendEmptyMessageDelayed(Startcheck, CheckPeriod);
                    break;
            }
        }
    };

    public void setChatlistener(ChatListener chatlistener) {
        this.chatlistener = chatlistener;
    }
    public boolean isConnect()
    {
        if(client!=null)
        {
            if(client.isConnecting())
                return true;
            else
                return false;
        }
        else
        {
            return false;
        }
    }
    public void CloseConnect()
    {
        if(client!=null)
            client.close();
        client=null;
    }
    public ChatMethod(Context context, String ...args)// 用户名 seesionid videoid Countryid
    {

        if(args.length!=4)
        {
            return;
        }
        this.args=args;
        this.context=context;
        ConnectService();
    }

    public void ConnectService()
    {
        if(Connectstate || args==null)return;
        if(!isNetworkAvailable(context) && context!=null)
        {
            networkstate_Record=NetUtil.NETWORK_NONE;
            ToastUtil.showMessage(context,context.getResources().getString(R.string.noconnection));
            return;
        }
        else
        {
            networkstate_Record=NetUtil.NETWORK_WIFI;
        }
        String template=args[0]+"&"+args[1]+"&"+args[2]+"&"+args[3];
        LogTools.e(tag,Httputils.WSBaseurl+template);
        try {
            Connectstate=true;
            client = new WebSocketClient(new URI(Httputils.WSBaseurl+template),new Draft_6455()) {

                @Override
                public void onOpen(ServerHandshake arg0) {
                    LogTools.e(tag, "打开链接");
                    Connectstate=false;
                        for (int i = 0; i < linkedList.size(); i++) {
                            client.send(linkedList.get(i));
                        }
                        linkedList.clear();
                    handler.sendEmptyMessage(Startcheck);
                }

                @Override
                public void onMessage(String arg0) {
                    LogTools.e(tag,"收到消息: "+arg0);
                    if(chatlistener!=null)
                    {
                        try {
                            chatlistener.OnReceviceMessage(ChatBean.Analysis(new JSONObject(arg0)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(Exception arg0) {
                    arg0.printStackTrace();
                    Connectstate=false;
                    LogTools.e(tag, "发生错误已关闭");
                }

                @Override
                public void onClose(int arg0, String arg1, boolean arg2) {
                    LogTools.e(tag, "链接已关闭");
                    Connectstate=false;
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    try {
                        LogTools.e(tag, new String(bytes.array(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }


            };
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Connectstate=false;
        }

        client.connect();
    }

    public boolean sendMessage(String message)
    {
        if(context==null)return false;
        if(LasttimeChat==0)
        {
            LasttimeChat=System.currentTimeMillis();
        }
        else
        {
            if(System.currentTimeMillis()-LasttimeChat<MessageSpeed)
            {
                ToastUtil.showMessage(context,context.getString(R.string.tip));
                return false;
            }
        }
        LasttimeChat=System.currentTimeMillis();
        handler.removeMessages(Startcheck);
        handler.sendEmptyMessage(Startcheck);
        if(client.isOpen())
        {
            if(message.length()==Heartbeat.length()&&message.equalsIgnoreCase(Heartbeat))
            {
                message=message+" ";
            }
            client.send(message);
            return true;
        }
        else
        {
            ConnectService();
            linkedList.add(message);
            return false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //检查网络状态的类型
            int netWrokState = NetUtil.getNetWorkState(context);
            if (netWrokState>-1 && this.context!=null && networkstate_Record<0)
                ConnectService();
        }
    }


    public interface ChatListener
    {
        public void OnReceviceMessage(ChatBean cbean);
    }


    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }


}
