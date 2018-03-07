package us.codecraft.webmagic.clawler;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by AnsonLiang on 2018/2/28 0028.
 */
public class YelpPipeline implements Pipeline {
    private String searchContent;
    private String location;


    public YelpPipeline(String searchContent, String location) {
        this.searchContent=searchContent;
        this.location=location;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String shopName = resultItems.get("shopName");
        String address = resultItems.get("address");
        String phone = resultItems.get("phone");
        String site = resultItems.get("site");
/*        YelpDomain yelpDomain=new YelpDomain(searchContent,location,shopName,address,phone,site);
        yelpDomainDao.saveAndFlush(yelpDomain);*/
    }
}
