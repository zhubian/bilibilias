package com.imcys.bilibilias.base.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.util.Log
import androidx.preference.PreferenceManager
import com.baidu.mobstat.StatService
import com.imcys.bilibilias.base.model.user.MyUserData
import com.imcys.bilibilias.base.utils.DownloadQueue
import org.xutils.x


class App : Application() {


    override fun onCreate() {
        super.onCreate()

        //百度统计开始
        startBaiDuService()

        handler = Handler(mainLooper)

        //xUtils初始化
        x.Ext.init(this)
        x.Ext.setDebug(false); // 是否输出debug日志, 开启debug会影响性能.


        context = this
    }

    /**
     * 百度统计
     */
    fun startBaiDuService() {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("baidu_statistics_type", false)) {
            StatService.setAuthorizedState(applicationContext, true)
        }else{
            StatService.setAuthorizedState(applicationContext, false)
        }
        StatService.autoTrace(applicationContext)

    }


    companion object {


        const val appSecret = "3c7c5174-a6be-4093-a0df-c6fbf7371480"
        const val AppGuideVersion = "1.0"

        val downloadQueue: DownloadQueue = DownloadQueue()

        //——————————————————cookie核心要素——————————————————
        lateinit var cookies: String
        lateinit var sessdata: String
        lateinit var biliJct: String
        //—————————————————————————————————————————————————

        //——————————————————全局线程处理器——————————————————
        lateinit var handler: Handler
        //—————————————————————————————————————————————————

        //——————————————————APP全局数据——————————————————
        lateinit var sharedPreferences: SharedPreferences

        //——————————————————B站视频模板——————————————————
        lateinit var videoEntry: String
        lateinit var videoIndex: String
        lateinit var bangumiEntry: String

        //——————————————————部分内置需要的上下文——————————————————
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var mid: Long = 0
        lateinit var myUserData: MyUserData.DataBean
        //—————————————————————————————————————————————————
    }

}