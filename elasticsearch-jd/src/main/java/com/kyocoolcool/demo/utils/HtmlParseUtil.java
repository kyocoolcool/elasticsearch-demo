package com.kyocoolcool.demo.utils;

import com.kyocoolcool.demo.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Chen https://blog.kyocoolcool.com
 * @version 1.0
 * @since 2020/11/17 5:31 PM
 **/
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        final List<Content> list = new HtmlParseUtil().parseHtml("java");
        list.forEach(System.out::println);
    }

    public List<Content> parseHtml(String keyword) throws IOException {
        //https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword="+keyword;
        final Document document = Jsoup.parse(new URL(url), 30000);
        Element element=document.getElementById("J_goodsList");
//        System.out.println(element.html());
        final Elements elements = element.getElementsByTag("li");
//        System.out.println(elements);
        final List<Content> goodList = new ArrayList<>();
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
//            if (img.equals("")) {
//                img = el.getElementsByTag("img").eq(1).attr("src");
//            }
            final String price = el.getElementsByClass("p-price").eq(0).text();
            final String title = el.getElementsByClass("p-name").eq(0).text();
            goodList.add(new Content(title, img, price));
        }
        return goodList;
    }
}
