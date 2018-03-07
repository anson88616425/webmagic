
package us.codecraft.webmagic.clawler;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.chainsaw.Main;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.HttpClientGenerator;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;


/**
 * Created by AnsonLiang on 2018/2/28 0028.
 */

public class YelpProcessor implements PageProcessor {
    private static String searchContent="vape shops";
/*    private static String location="New York,Los Angeles,Chicago,Houston,Philadelphia,Phoenix,San Antonio,San Diego,Dallas,San Jose," +
            "Jacksonville,Indianapolis,San Francisco,Austin,Columbus,Charlotte,Fort Worth,Detroit,Memphis,Boston";*/

    private static String location="San Antonio";

    private Integer order=1;

    private Site site = Site
            .me()
            .setDomain("yelp.com")
            .setSleepTime(300)
            .setUserAgent(
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");

    @Override
    public void process(Page page) {
        String shopName="";
        if ((page.getUrl()).regex(
                "https://www.yelp.com/search\\?find_desc=.+&find_loc=.+")
                .match()) {//店铺列表

            page.addTargetRequests(page.getHtml()//店铺详细加入爬虫队列
                    .xpath("//h3[@class=\"search-result-title\"]")
                    .links().all());

            page.addTargetRequests(page.getHtml()//分页加入爬虫列表
                    .xpath("//div[@class=\"page-option\"]")
                    .links().all());
                //设置skip之后，这个页面的结果不会被Pipeline处理


        }
        else if((page.getUrl()).regex(//店铺详细
                "https://www.yelp.com/\\w+")
                .match()) {
            shopName=page.getHtml()
                    .xpath("//h1[@class='biz-page-title']//text()")
                    .toString();
            if(shopName==null)  {
                page.addTargetRequests(page.getHtml()//跳转页加入爬虫
                        .xpath("//a[@rel='nofollow']//text()").all());
                page.setSkip(true);
            }
            else {
                System.out.println(order+"---------------------"+shopName);
                order++;
            }
            page.putField("shopName",page.getHtml()
                    .xpath("//h1[@class='biz-page-title']//text()")
                    .toString());

            page.putField("address",page.getHtml()
                    .xpath("//div[@class='map-box-address']//address//text()")
                    .toString());

            page.putField("phone",page.getHtml()
                    .xpath("//span[@class='biz-phone']//text()")
                    .toString());
            String site=page.getHtml()
                    .xpath("//[@class='biz-website']//a//text()")
                    .toString();
            page.putField("site",site);
            if(StringUtils.isNotBlank(site)) {

                Page page1=sendDownload(site);
                List<String> list=page1.getHtml().regex("[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?").all();
                List newList = new ArrayList(new TreeSet(list));
            }

        }

    }

    public Page sendDownload(String site) {
        String[] ips=getRadomIp();
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy(ips[0],Integer.parseInt(ips[1]),"edazonetime","Eda88888888")));
        Request request=new Request("http://"+site);
        Page page1 = null;
        try {
            page1 = httpClientDownloader.download(request, Site.me().setSleepTime(300).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36").toTask());
        } catch (Exception e) {
            if("Read timed out".equals(e.getMessage())) {
                return sendDownload(site);
            }
        }
        if(page1.getStatusCode()==503) {
            return sendDownload(site);
        }
       return page1;
    }

    public static String[] getRadomIp() {
        InputStream input = null;
        String[] ipAndProtArrary={};
        try {
            File file = new File("E:\\edaWork\\provxIp.xls");
            input = new FileInputStream(file);
            Workbook readxls = Workbook.getWorkbook(input);
            Random rand = new Random();

            int randNum = rand.nextInt(48);
            Sheet readsheet = readxls.getSheet(0);
            Cell cell = readsheet.getCell(0, randNum);
            String ipAndProt = cell.getContents();
            ipAndProtArrary=ipAndProt.split(":");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ipAndProtArrary;
    }

    public static void main(String[] args) {
            sendRequestTask();
    }

    public static void sendRequestTask() {
        String[] ips=getRadomIp();
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy(ips[0],Integer.parseInt(ips[1]),"edazonetime","Eda88888888")));
        try {
            Spider.create(new YelpProcessor())
                    .addPipeline(new YelpPipeline(searchContent,location))
                    .setDownloader(httpClientDownloader)
                    .thread(20)
                    .addUrl("https://www.yelp.com/search?find_desc="+searchContent+"&find_loc="+location).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}

