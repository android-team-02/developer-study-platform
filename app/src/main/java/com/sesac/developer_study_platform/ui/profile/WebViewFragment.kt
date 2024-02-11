package com.sesac.developer_study_platform.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.sesac.developer_study_platform.R

class WebViewFragment : Fragment() {

    private val args by navArgs<WebViewFragmentArgs>()
    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView(view)
    }

    private fun setupWebView(view: View) {
        webView = view.findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(args.url)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView.destroy()
    }

}