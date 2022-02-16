package com.by.basedialog.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.text.TextUtils
import java.lang.Exception
import java.util.*

/**
 *
 * @author Catherine Liu
 * 2021/12/20 10:43
 * 管理当前的Activity
 *
 **/
class ActivityUtil {
    private val TAG = ActivityUtil::class.simpleName
    /**
     * Activity堆栈 Stack:线程安全
     */
    var mActivityStack: Stack<Activity> = Stack()

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity?) {
        mActivityStack.add(activity)
    }

    /**
     * 移除堆栈中的Activity
     * @param activity Activity
     */
    fun removeActivity(activity: Activity?) {
        if (activity != null && mActivityStack.contains(activity)) {
            mActivityStack.remove(activity)
        }
    }

    /**
     * 获取当前Activity (堆栈中最后一个添加的)
     * @return Activity
     */
    val currentActivity: Activity?
        get() = mActivityStack.lastElement()

    /**
     * 获取指定类名的Activity
     */
    fun getActivity(cls: Class<*>?): Activity? {
        for (activity in mActivityStack) {
            if (activity::class == cls) {
                return activity
            }
        }
        return null
    }

    /**
     * 结束当前Activity (堆栈中最后一个添加的)
     */
    fun finishCurrentActivity() {
        val activity: Activity = mActivityStack.lastElement()
        finishActivity(activity)
    }

    /**
     * 结束指定的Activity
     * @param activity Activity
     */
    fun finishActivity(activity: Activity?) {
        if (activity != null && mActivityStack.contains(activity)) {
            mActivityStack.remove(activity)
            activity.finish()
        }
    }

    /**
     * 结束指定类名的Activity
     * @param clazz Activity.class
     */
    fun finishActivity(clazz: Class<*>?) {
        for (activity in mActivityStack) {
            if (activity::class == clazz) {
                finishActivity(activity)
                break
            }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        for (i in mActivityStack.size - 1 downTo 0) {
            if (mActivityStack[i] != null) {
                finishActivity(mActivityStack[i])
            }
        }
        mActivityStack.clear()
    }

    /**
     * 结束某个Activity之外的所有Activity
     */
    fun finishAllActivityExcept(clazz: Class<*>?) {
        for (i in mActivityStack.size - 1 downTo 0) {
            if (mActivityStack[i] != null && mActivityStack[i]::class != clazz) {
                finishActivity(mActivityStack.get(i))
            }
        }
    }

    /**
     * 退出应用程序
     */
    fun exitApp() {
        try {
            finishAllActivity()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            System.exit(0)
        }
    }

    companion object {
        /**
         * 单一实例
         */
        private var sActivityUtil: ActivityUtil? = null

        /**
         * 获取单一实例 双重锁定
         * @return this
         */
        val instance: ActivityUtil
            get() {
                if (sActivityUtil == null) {
                    synchronized(ActivityUtil::class.java) {
                        if (sActivityUtil == null) {
                            sActivityUtil = ActivityUtil()
                        }
                    }
                }
                return sActivityUtil!!
            }

        /**
         * 判断某个Activity 界面是否在前台
         * @param context
         * @param className 某个界面名称
         * @return
         */
        fun isForeground(context: Context?, className: String): Boolean {
            if (context == null || TextUtils.isEmpty(className)) {
                return false
            }
            val am: ActivityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val list: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(1)
            if (list.isNotEmpty()) {
                val cpn: ComponentName? = list[0].topActivity
                if (className == cpn?.className) {
                    return true
                }
            }
            return false
        }

        fun getTopActivity(): Activity?{
            for (activity in instance.mActivityStack) {
               if(!isActivityAlive(activity)){
                  continue
               }
               return activity
            }
            return null
        }
         fun isActivityAlive(activity: Activity?): Boolean {
            return (activity != null && !activity.isFinishing
                    && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !activity.isDestroyed))
        }
    }
}