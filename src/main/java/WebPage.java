import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebPage {
    private static final String TXT_FILE = "data/text.txt";

    public String getADDRESS() {
        return ADDRESS;
    }

    private final String ADDRESS;
    private List<WebPage> childrenList;
    private List<WebPage> parentList;

    private static Set<WebPage> connectedList = new HashSet<>();

    public WebPage(String address){
        this.ADDRESS = address;
        childrenList = new ArrayList<>();
        parentList = new ArrayList<>();
    }

    public List<WebPage> getChildrenList() {
        return childrenList;
    }

    public synchronized void setChildrenList(List<WebPage> childrenList) {
        this.childrenList = childrenList;
    }

    public List<WebPage> getParentList() {
        return parentList;
    }

    public synchronized void setParentList(List<WebPage> parentList) {
        this.parentList = parentList;
    }

    public Set<WebPage> getConnectedList() {
        return connectedList;
    }

    public synchronized static void setConnectedList(Set<WebPage> connectedList) {
        WebPage.connectedList = connectedList;
    }
    public void parse(){
        try {
            Document document = Jsoup.connect(ADDRESS).get();
            connectedList.add(this);
            Thread.sleep(600);
            Elements links = document.select("a[href]");
            for (Element link : links){
                String pageAddress = link.attr("abs:href");
                if (pageAddress.startsWith(parentList.isEmpty() ? ADDRESS : parentList.get(0).getADDRESS()) &&
                        !containsInvalidSymbol(pageAddress) ) {
                    WebPage webPage = new WebPage(pageAddress);
                    if (parentList.contains(webPage) || webPage.equals(this) ||
                            this.childrenList.contains(webPage)) {
                        continue;
                    }
                    webPage.parentList.addAll(this.parentList);
                    webPage.parentList.add(this);
                    childrenList.add(webPage);
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private boolean containsInvalidSymbol(String address){
        return address.contains("#");
    }
}
