package ir.alirezahp.streamevideo.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import ir.alirezahp.streamevideo.dialog.LoadingDialog
import ir.alirezahp.streamevideo.helper.G

open class BaseActivity : AppCompatActivity() {

    private var loadingDialog: LoadingDialog? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(G.changeLanguageToFarsi(newBase))
    }

    protected fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)
        }
        loadingDialog?.show()
    }

    protected fun hideLoading() {
        loadingDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}