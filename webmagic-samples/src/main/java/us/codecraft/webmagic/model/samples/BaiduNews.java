package us.codecraft.webmagic.model.samples;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;

import java.util.List;

/**
 * @author code4crafter@gmail.com
 */
public class BaiduNews {

    @ExtractBy(value = "//h3[@class='c-title']/a/text()",multi = true)
    private List<String> name;

    @ExtractBy(value = "//div[@class='c-summary']/text()",multi = true)
    private List<String> description;

    @Override
    public String toString() {
        return "BaiduNews{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public static void main(String[] args) {
        OOSpider ooSpider = OOSpider.create(Site.me().setCharset("utf-8").setSleepTime(1), BaiduNews.class);
        //single download
        BaiduNews baike = ooSpider.get("http://news.baidu.com/ns?tn=news&cl=2&rn=20&ct=1&fr=bks0000&ie=utf-8&word=httpclient");
        System.out.println(baike);

        ooSpider.close();
    }

    public List<String> getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }
}