package com.by.basedialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.by.dialog.utils.ActivityUtil
import com.by.update.entity.AppUpdate
import com.by.update.utils.UpdateManager
import com.yf.accelerator.update.listener.DialogDismissCallBack
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var updateManager: UpdateManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtil.instance.addActivity(this)
        setContentView(R.layout.activity_main)
        tvSurpriseMe.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog(){
        updateManager = UpdateManager()
        // 更新的数据参数
        val appUpdate: AppUpdate = AppUpdate.Builder()
            .newVersionUrl("http://10.191.80.221:6022/app-dev-debug-2.0.2-keepalive.apk") //更新地址（必传）
            .newVersionCode("2.0.1") // 版本号（非必填）
            .updateResourceId(R.layout.dialog_upgrade) // 通过传入资源id来自定义更新对话框，注意取消更新的id要定义为btnUpdateLater，立即更新的id要定义为btnUpdateNow（非必填）
            .updateTitle(R.string.update_title) // 更新的标题，弹框的标题（非必填，默认为应用更新）
//            .updateContentTitle(R.string.update_content_lb) // 更新内容的提示语，内容的标题（非必填，默认为更新内容）
            .updateInfo("喀喀喀，我是卖报的小行家~") // 更新内容（非必填，默认“1.用户体验优化\n2.部分问题修复”）
//            .fileSize("5.8M") // 文件大小（非必填）
//            .savePath("/A/B") // 保存文件路径（默认前缀：Android/data/包名/files/ 文件名：download）
            .isSilentMode(false) //是否采取静默下载模式（非必填，只显示更新提示，后台下载完自动弹出安装界面），否则，显示下载进度，显示下载失败
            .forceUpdate(0) //是否强制更新（非必填，默认不采取强制更新，否则，不更新无法使用）
//            .md5("")//文件的MD5值，默认不传，如果不传，不会去验证md5(非静默下载模式生效，若有值，且验证不一致，会启动浏览器去下载)
            .build()
        updateManager!!.startUpdate(this, appUpdate,object : DialogDismissCallBack {
            override fun dismiss(dismissType: String) {
                //非强制更新，若用户点击关闭退出更新，72小时内不要弹出更新提醒
                if (dismissType == DialogDismissCallBack.cancelUpdate){
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        updateManager?.dismissAllDialog()
    }
}