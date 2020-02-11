import java.util.Comparator;


import components.map.Map;
import components.map.Map1L;
import components.map.Map.Pair;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.sortingmachine.SortingMachine;
import components.sortingmachine.SortingMachine3;
import components.utilities.Reporter;

/**
 * Put a short phrase describing the program here.
 * 
 * @author David Piatt & Paul Shortman
 * 
 */
public final class TagCloudGenerator {

    //a
    private static final int A = 11;
    
    //b
    private static final int B = 48;
    
    /**
     * 
     * Comparitor class to compare the queue to alphabetize it. 
     *
     *
     */
    
    public static class PairComparator implements Comparator<Map.Pair<String,Integer>>{
        
        
        @Override
        public int compare(Pair<String, Integer> arg0, Pair<String, Integer> arg1) {
            return arg0.key().compareToIgnoreCase(arg1.key());
        }
        
    }
    
    
    /**
     * Comparitor class to sort queue by number of occurances
     */
    
    public static class OccuranceComparator implements Comparator<Map.Pair<String, Integer>>{
        @Override
        public int compare(Pair<String, Integer> arg0, Pair<String, Integer> arg1){
            return arg1.value().compareTo(arg0.value()); 
        }
    }
    
    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGenerator() {
    }

    
    /**
     * Make a map of the input file to the occurances of each individual word. 
     * 
     * @param filename
     *  file name for the input file
     * 
     * @param input
     *  A SimpleReader to read in the file
     * 
     * 
     * 
     * 
     * 
     * @ensures [map contains words and occurances]
     */
    
    public static Map<String, Integer> getCountMap(SimpleReader input) {
        
        //All of the terms are mapped with their occurances using nextWordOrSeparator. 
        
        int index = 0;
        
        //decalre a map. 
        Map<String, Integer> countMap = new Map1L<String,Integer>();
        
        String separators = " ,.?/!-\t&:;";
        
        String s = "";
        
        while( !input.atEOS() ){
            s+= " ";
            s += input.nextLine();
            
        }
        
       //go through the string that is the whole document. 
        //if the term is not in the map add it and set occurance to one
        //else increment occurance of term
        while(index < s.length()-1){
            String nextWord = nextWordOrSeparator(s,index);
            index += nextWord.length();
            if(!countMap.hasKey(nextWord) && !separators.contains(nextWord.substring(0,1))){
                countMap.add(nextWord, 1);
            }else if(!separators.contains(nextWord.substring(0,1))){
                countMap.replaceValue(nextWord, countMap.value(nextWord) + 1);
            }
        }
        
        return countMap;

    }
    
    
    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     * 
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */

    private static String nextWordOrSeparator(String text, int position) {
        
        
        
        final String separatorStr = " ,.?/!-\t";

        //set up separator set
        Set<Character> separators = new Set1L<>();
        
        separators.add(' ');
        separators.add(',');
        separators.add('.');
        separators.add('?');
        separators.add('/');
        separators.add('!');
        separators.add('-');
        separators.add('\t');
        separators.add('&');
        separators.add(':');
        separators.add(';');
        separators.add('\"');
        separators.add(']');
        separators.add('[');
        
        
        
        
        //return the next word or separator with toReturn;
        
        
        //checking the characters to see if they are separators, updating the position of  
        // next word or separator. 
        
        String toReturn = "";
        int index = position;
        char tmp = text.charAt(index);

        if(separators.contains(tmp) && index < text.length()){
            while(separators.contains(tmp) && index < text.length()){
                index++;
                if(index < text.length()){
                    tmp = text.charAt(index);
                    
                }
            }
        } else {
            while(!separators.contains(tmp)&& index < text.length()){
                index ++;
                if(index < text.length()){
                    tmp = text.charAt(index);

                }
            }
        }
        
        
        return text.substring(position, index);
    }
    
    
    /**
     * getSortedQueue will sort the queue of maps by alphabetical order. It uses the comparator class
     * @param map
     *  the map of terms and their occurances.
     * @return
     *  Queue that is of sorted maps. 
     */
    
    public static Queue<Map.Pair<String,Integer>> getSortedQueue(Map<String,Integer> map){
       Queue<Map.Pair<String, Integer>> pairs = new Queue1L<>();
       
       //build the queue from the map
       while(map.size() > 0){
           pairs.enqueue(map.removeAny());
          }
          pairs.sort(new PairComparator());
          return pairs;

    }
    
    
    
    
    public static void outputHeader(SortingMachine <Map.Pair<String,Integer>> sm ,SimpleWriter out, String fileName, int desiredWords){
       
        out.println("<html>");
        out.println("<head>");
        out.println("<title>"+ desiredWords +"Words counted from: " + fileName + "</title>");
        out.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/"
                        + "web-sw2/assignments/projects"
                        + "/tag-cloud-generator/data/tagcloud.css\""
                        + " rel=\"stylesheet\"" + " type=\"text/css\">");

        out.println("<body>");
        out.println("<h2>"+ desiredWords +"Words counted from: " + fileName + "</h2>");
        out.println("<hr>");
       
        
    }
    
    public static int fontSize(int x, int relMin, int relMax){
        
        return (((B-A) * (x-relMin)) /  (relMax - relMin)) + A;
    }
    
    public static void outputWordCloud(SortingMachine <Map.Pair<String,Integer>> sm ,SimpleWriter out, String fileName, int desiredWords){
      //New Sorting machine with alphabetical comparator. 
        
        out.println("<div class = \"cdiv\">");
        out.println("<p class = \"cbox\">");
        
        SortingMachine<Map.Pair<String, Integer>> sm2 = new SortingMachine3<>(new PairComparator());
      
        Map<String, Integer> countMap = new Map1L<String,Integer>();
        
        //populate sorting machine
        int count = 0;
        while(count < desiredWords){
            //find the relative size of the word 
            //print it out. 
            Map.Pair<String, Integer> removed = sm.removeFirst();
            sm2.add(removed);
            countMap.add(removed.key(), removed.value());
            count++;   
        }
        //sort the machine
        sm2.changeToExtractionMode();
        
        
        
        int relMin=Integer.MAX_VALUE;
        int relMax = 0;
        //System.out.println(countMap.toString());
        for(Map.Pair<String, Integer> m : countMap){
            if(m.value() < relMin){
                //System.out.println(m.value());
                relMin = m.value();
            }
            
            if(m.value() > relMax){
                relMax = m.value();
            }
        }
        
        
        
        
        
        while(sm2.size() > 0){
            Map.Pair<String, Integer> removed = sm2.removeFirst();
            int fontsize = fontSize(removed.value(), relMin, relMax);
            out.println("<span style=\"cursor:default\"class=\"" + "f" + fontsize + "\" title=\"count: " + removed.value() + "\">" + removed.key() + "</span>");
        }
        
       
    }
    
    public static void outputFooter(SortingMachine <Map.Pair<String,Integer>> sm ,SimpleWriter out, String fileName, int desiredWords){

        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
        
    }
    
    
    
    /**
     * 
     * outputHTML will build the html webpage for the occurances of the words.
     * 
     * @param out
     *  out writer to print out the file information. 
     * @param fileName
     *  the file name to print to the screen.
     */
    
    /**
     * Main method.
     * 
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        out.println("Welcome to David and Paul's word cloud generator!");
        
        out.println("");
        out.println("");
        
        //Get in file
        out.println("Please enter in a file name to make the word cloud: ");
        String fileName = in.nextLine();
        
        //get printing location
        out.println("Please enter an output file name: ");
        String outputFileName = in.nextLine();
        
        //get number of desired words in cloud
        out.println("How many words would you like in the cloud?");
        int desiredWords = in.nextInteger();
        
        Reporter.assertElseFatalError(desiredWords>=0, "Input a positive number, ya dildo.");
        
        //create writer from new file
        SimpleWriter newOut = new SimpleWriter1L(outputFileName);
        
        //make reader to get the content in the file.
        SimpleReader newIn = new SimpleReader1L(fileName);
        
        //make a map with the counted terms.
        Map<String, Integer> countMap = getCountMap(newIn);
        
        //now we make a queue of maps. then for the pair we can just call the key and value. We'll
        //sort the queue so everything is nice and alphabetical. God forbid there be no order.
        
        SortingMachine<Map.Pair<String, Integer>> sm = new SortingMachine3<>(new OccuranceComparator());
        
        while(countMap.size()>0){
            sm.add(countMap.removeAny());
        }
        sm.changeToExtractionMode();
        
        //Queue<Map.Pair<String, Integer>> pairs = getSortedQueue(countMap);
        
        
        outputHeader(sm, newOut, fileName, desiredWords);
        outputWordCloud(sm, newOut, fileName, desiredWords);
        outputFooter(sm, newOut, fileName, desiredWords);
        
        
        newIn.close();
        newOut.close();
        in.close();
        out.close();
        
        
        
        
    }

}
