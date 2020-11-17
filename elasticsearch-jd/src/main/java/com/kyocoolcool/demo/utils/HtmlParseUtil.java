package com.kyocoolcool.demo.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Chris Chen https://blog.kyocoolcool.com
 * @version 1.0
 * @since 2020/11/17 5:31 PM
 **/
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        //https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword=java";
        final Document document = Jsoup.parse(new URL(url), 30000);
        Element element=document.getElementById("J_goodsList");
        System.out.println(element.html());
        final Elements elements = element.getElementsByTag("li");
//        System.out.println(elements);
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
//            if (img.equals("")) {
//                img = el.getElementsByTag("img").eq(1).attr("src");
//            }
            final String price = el.getElementsByClass("p-price").eq(0).text();
            final String title = el.getElementsByClass("p-name").eq(0).text();
            System.out.println("==========");
            System.out.println(img);
            System.out.println(price);
            System.out.println(title);
        }
    }
}
