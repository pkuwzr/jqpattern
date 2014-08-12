package pm;

/**
 * @author Wang Zerui
 * A search result includes a simple pattern
 * and its similarity with the target pattern.
 */
public class SearchResult {
    private SimplePattern pattern;
    private double similarity;

    public SearchResult(SimplePattern sp, double sim) {
        pattern = sp;
        similarity = sim;
    }

    public SimplePattern getPattern() {
        return pattern;
    }

    public double getSimilarity() {
        return similarity;
    }
}
