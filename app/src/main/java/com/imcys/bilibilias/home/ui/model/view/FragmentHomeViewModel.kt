package com.imcys.bilibilias.home.ui.model.view

import android.content.Intent
import android.net.Uri
import android.view.View
import com.imcys.bilibilias.base.api.BilibiliApi
import com.imcys.bilibilias.base.app.App
import com.imcys.bilibilias.base.utils.DialogUtils
import com.imcys.bilibilias.home.ui.activity.DedicateActivity
import com.imcys.bilibilias.home.ui.activity.DonateActivity
import com.imcys.bilibilias.utils.http.HttpUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.system.exitProcess

class FragmentHomeViewModel {


    fun goToPrivacyPolicy(view: View) {
        val uri =
            Uri.parse("https://docs.qq.com/doc/p/080e6bdd303d1b274e7802246de47bd7cc28eeb7?dver=2.1.27292865")
        val intent = Intent(Intent.ACTION_VIEW, uri);
        view.context.startActivity(intent)
    }

    fun goToNewVersionDoc(view: View) {
        val uri = Uri.parse("https://docs.qq.com/doc/DVXZNWUVFakxEQ2Va")
        val intent = Intent(Intent.ACTION_VIEW, uri);
        view.context.startActivity(intent)
    }

    fun goToDedicatePage(view: View) {
        val intent = Intent(view.context, DedicateActivity::class.java)
        view.context.startActivity(intent)
    }

    fun goToDonate(view: View) {
        val intent = Intent(view.context, DonateActivity::class.java)
        view.context.startActivity(intent)
    }

    fun toDonateList(view: View) {
        val uri = Uri.parse("https://api.misakamoe.com/as-donate.html")
        val intent = Intent(Intent.ACTION_VIEW, uri);
        view.context.startActivity(intent)
    }

    fun goToCommunity(view: View) {
        val uri = Uri.parse("https://support.qq.com/product/337496")
        val intent = Intent(Intent.ACTION_VIEW, uri);
        view.context.startActivity(intent)
    }



    fun logoutLogin(view: View) {
        DialogUtils.dialog(
            view.context,
            "退出登录",
            "确定要退出登录了吗？",
            "是的",
            "点错了",
            true,
            positiveButtonClickListener =
            {
                HttpUtils.addHeader("cookie",App.cookies).addParam("biliCSRF", App.biliJct)
                    .post(BilibiliApi.exitLogin, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            exitProcess(0)
                        }

                    })
            },
            negativeButtonClickListener = {}
        ).show()
    }


}