package fine.koaca.wms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WebList extends AppCompatActivity {
    WebView webView;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_list);
        webView=findViewById(R.id.web_list);
        Intent intent=getIntent();
        String blList=intent.getStringExtra("bl");


        webView.setWebViewClient(new WebViewClient());
        webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if(blList==null){
            webView.loadUrl("https://play.google.com/store/apps/details?id=fine.koaca.wms");

        }else{
           webView.loadUrl("https://www.tradlinx.com/unipass?type=2&blNo="+blList+"&blYr=2021");}

    }
}