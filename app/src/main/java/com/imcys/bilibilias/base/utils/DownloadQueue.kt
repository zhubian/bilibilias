package com.imcys.bilibilias.base.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import com.imcys.bilibilias.base.app.App
import com.imcys.bilibilias.base.model.user.DownloadTaskDataBean
import org.xutils.common.Callback
import org.xutils.common.task.PriorityExecutor
import org.xutils.http.RequestParams
import org.xutils.x
import java.io.File


// 定义一个下载队列类
class DownloadQueue {


    var recyclerView: RecyclerView? = null

    // 存储待下载的任务
    private val queue = mutableListOf<Task>()

    // 当前正在下载的任务
    private val currentTasks = mutableListOf<Task>()

    // 下载任务类
    inner class Task(
        // 下载地址
        val url: String,
        // 下载文件保存路径
        val savePath: String,
        // 文件类型，0为视频，1为音频
        var fileType: Int,
        //下载任务的其他参撒
        val downloadTaskDataBean: DownloadTaskDataBean,
        // 定义下载完成回调
        val onComplete: (Boolean) -> Unit,
        // 定义当前任务的下载进度
        var progress: Double = 0.0,
        // 定义当前文件大小
        var fileSize: Double = 0.0,
        // 定义当前已经下载的大小
        var fileDlSize: Double = 0.0,
        // 定义当前任务的下载请求
        var call: Callback.Cancelable? = null,

        )


    // 添加下载任务到队列中
    fun addTask(
        url: String,
        savePath: String,
        fileType: Int,
        downloadTaskDataBean: DownloadTaskDataBean,
        onComplete: (Boolean) -> Unit,
    ) {
        // 创建一个下载任务
        val task = Task(url, savePath, fileType, downloadTaskDataBean, onComplete)
        // 添加下载任务到队列中
        queue.add(task)

        // 如果队列不为空，就执行队列中的所有任务
        if (queue.isNotEmpty()) {
            executeTask()
        }
    }


    // 执行下载任务
    private fun executeTask() {
        //刷新下载对象
        val executor = PriorityExecutor(2, true)

        while (currentTasks.size < 3 && queue.isNotEmpty()) {
            //删除并且返回当前的task
            val task = queue.removeAt(0)
            // 添加任务到当前任务列表中
            currentTasks.add(task)
            // 创建一个 RequestParams 对象，用来指定下载地址和文件保存路径
            val params = RequestParams(task.url)
            //设置header头
            params.addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0");
            params.addHeader("referer", "https://www.bilibili.com/")
            params.addHeader("cookie", App.cookies)
            //设置是否根据头信息自动命名文件
            params.isAutoRename = false
            //设储存路径
            params.saveFilePath = task.savePath
            //自定义线程池,有效的值范围[1, 3], 设置为3时, 可能阻塞图片加载.
            //params.executor = PriorityExecutor(1, true)
            //是否可以被立即停止.
            params.isCancelFast = true


            //使用多个线程同步下载
            executor.execute {
                // 使用 XUtils 库来下载文件
                task.call = x.http().get(params, object : Callback.ProgressCallback<File> {
                    override fun onSuccess(result: File?) {
                        //移除下载任务
                        currentTasks.remove(task)
                        // 下载成功，调用任务的完成回调
                        task.onComplete(true)
                        // 执行下一个任务
                        executeTask()
                    }

                    override fun onError(ex: Throwable?, isOnCallback: Boolean) {
                        //移除下载任务
                        currentTasks.remove(task)
                        // 下载失败，调用任务的完成回调
                        task.onComplete(false)
                        // 执行下一个任务
                        executeTask()
                    }

                    override fun onCancelled(cex: Callback.CancelledException?) {
                        //移除下载任务
                        currentTasks.remove(task)
                        // 下载取消，调用任务的完成回调
                        task.onComplete(false)
                        // 执行下一个任务
                        executeTask()
                    }

                    override fun onFinished() {
                        // 暂时不需要实现
                    }

                    override fun onWaiting() {
                        // 暂时不需要实现
                    }

                    override fun onStarted() {
                    }

                    override fun onLoading(total: Long, current: Long, isDownloading: Boolean) {
                        //更新进度
                        updateProgress(task, (current * 100 / total).toDouble())
                        asLogI("下载回调", task.progress.toString())
                        task.fileSize = (total / 1048576).toDouble()
                        task.fileDlSize = (current / 1048576).toDouble()
                    }

                })
            }


        }


    }

    // 在 DownloadQueue 类中
    @SuppressLint("NotifyDataSetChanged")
    fun updateProgress(task: Task, progress: Double) {
        // 更新当前任务的下载进度
        task.progress = progress
        // 通知 RecyclerView 适配器数据发生了改变
        recyclerView?.adapter?.notifyDataSetChanged()
    }
}


