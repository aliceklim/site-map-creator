import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class SiteMapCreator extends RecursiveTask<List<String>> {
    public static  Set<WebPage> connectedList = Collections.synchronizedSet(new HashSet<>());
    public static  Set<String> processedLinks = Collections.synchronizedSet(new HashSet<>());
    private WebPage webPage;
    private static List<String> linksToPrint = new ArrayList<>();;
    private static List<String> linksToPrintSafeList = Collections.synchronizedList(linksToPrint);;

    public SiteMapCreator(WebPage webPage) {
        this.webPage = webPage;
    }

    @Override
    protected List<String> compute() {
        String address = webPage.getADDRESS();
        List<SiteMapCreator> taskList = new ArrayList<>();
        webPage.parse();
        for (WebPage page : webPage.getChildrenList()) {
            connectedList.addAll(page.getConnectedList());
            if (page.getParentList().contains(page) || page.equals(webPage)) {
                continue;
            }
            SiteMapCreator smcTask = new SiteMapCreator(page);
            smcTask.fork();
            taskList.add(smcTask);
            addToPrintListIfProcessed(page.getADDRESS());
        }
        for (SiteMapCreator task : taskList) {
            linksToPrintSafeList.addAll(task.join());
        }
        return linksToPrintSafeList;
    }
    private synchronized void addToPrintListIfProcessed(String webPage){
        if (!SiteMapCreator.processedLinks.contains(webPage)){
            SiteMapCreator.processedLinks.add(webPage);
            char someChar = '/';
            int count = 0;
            for (int i = 0; i < webPage.length(); i++) {
                if (webPage.charAt(i) == someChar) {
                    count++;
                }
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++ ){
                sb.append("\t");
            }
            System.out.println(sb.toString() + webPage);
        }
    }
    private void writeToFile(String s){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/text.txt"));
            writer.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
