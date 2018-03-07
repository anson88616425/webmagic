package us.codecraft.webmagic.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * Created by AnsonLiang on 2018/2/27 0027.
 */
public class Test  implements PageProcessor{

    private Site site = Site
            .me()
            .setDomain("yelp.com")
            .setSleepTime(300)
            .setUserAgent(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");

    @Override
    public void process(Page page) {
        if ((page.getUrl()).regex(
                "https://www.yelp.com/search\\?find_desc=\\w+&find_loc=.+")
                .match()) {//店铺列表
            page.addTargetRequests(page.getHtml()//店铺详细加入爬虫队列
                    .xpath("//span[@class=\"indexed-biz-name\"]")
                    .links().all());
            page.addTargetRequests(page.getHtml()//店铺详细加入爬虫队列
                    .xpath("//div[@class=\"page-option\"]")
                    .links().all());


        }
        else if((page.getUrl()).regex(//店铺详细
                "https://www.yelp.com/\\w+")
                .match()) {
            page.putField("shopName",page.getHtml()
                    .xpath("//h1[@class='biz-page-title']//text()")
                    .toString());

            page.putField("address",page.getHtml()
                    .xpath("//div[@class='map-box-address']//address//text()")
                    .toString());

            page.putField("phone",page.getHtml()
                    .xpath("//span[@class='biz-phone']//text()")
                    .toString());

            page.putField("site",page.getHtml()
                    .xpath("//[@class='biz-website']//a//text()")
                    .toString());
        }


    }

    public static void main(String[] args) {
        Spider.create(new Test())
                .addPipeline(new JsonFilePipeline("D:\\webmagic\\"))
                .addUrl("https://www.yelp.com/search?find_desc=Food&find_loc=San Francisco, CA").run();
    }

    @Override
    public Site getSite() {
        return site;
    }
}
