// Lịch Việt - Phần mềm được phát triển bởi Lâm Mạnh - TTYT Tương Dương © 2026
package vn.tuongduong.lichviet

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : ComponentActivity() {
    private lateinit var web: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        web = WebView(this)
        // WebView bỏ qua padding của chính nó, nên đặt trong một khung và chừa lề cho khung đó.
        val root = FrameLayout(this)
        root.addView(web, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        setContentView(root)

        // Chừa chỗ cho thanh trạng thái, tai thỏ và thanh điều hướng/cử chỉ trên mọi máy.
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val b = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.setPadding(b.left, b.top, b.right, b.bottom)
            WindowInsetsCompat.CONSUMED
        }

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowContentAccess = false
            setSupportZoom(false)
        }
        web.webViewClient = object : WebViewClient() {
            // Ứng dụng hoạt động hoàn toàn ngoại tuyến: không mở trang web ngoài.
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) =
                !request.url.toString().startsWith("file:///android_asset/")
        }
        web.loadUrl("file:///android_asset/index.html")

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Cho trang web đóng màn hình chi tiết / tra cứu trước; nếu không còn gì để đóng thì thoát.
                web.evaluateJavascript("window.onBack ? window.onBack() : false") { r ->
                    if (r != "true") {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        LichVietWidget.refreshAll(this)
        // Mở lại sau khi sang ngày mới: làm mới "hôm nay".
        if (::web.isInitialized) web.evaluateJavascript("window.onResumeApp && window.onResumeApp()", null)
    }

    override fun onDestroy() {
        web.destroy()
        super.onDestroy()
    }
}
