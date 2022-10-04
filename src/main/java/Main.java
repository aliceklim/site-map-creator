import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        new ForkJoinPool().invoke(new SiteMapCreator(new WebPage("https://lenta.ru/")));
        System.out.println(SiteMapCreator.processedLinks.size());
    }
}

