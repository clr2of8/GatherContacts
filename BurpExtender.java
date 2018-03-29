package burp;

import java.util.Arrays;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BurpExtender implements IBurpExtender, IProxyListener
{
    private IBurpExtenderCallbacks callbacks;
    private static IExtensionHelpers helpers;
    private final String version;
    private final String name;
    private PrintWriter stdout;
    private ArrayList<String> dupeList = new ArrayList<String>();

    public BurpExtender()
    {
        this.name = "Gather Contacts";
        this.version = "0.1";
    }

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
        this.callbacks = callbacks;

        callbacks.setExtensionName(this.name + " " + this.version);
        //callbacks.printOutput(this.name + " " + this.version);
        //callbacks.printOutput("Carrie Roberts @orOneEqualsOne ");
        helpers = callbacks.getHelpers();
        
        // obtain our output stream
        stdout = new PrintWriter(callbacks.getStdout(), true);
        
        // register ourselves as a Proxy listener
        callbacks.registerProxyListener(this);

    }
    
    //
    // implement IProxyListener
    //

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message)
    {
        if(!messageIsRequest){
            byte[] response = (message.getMessageInfo()).getResponse();
            IResponseInfo analyzedResponse = helpers.analyzeResponse(response);
            String bodyStr = helpers.bytesToString(Arrays.copyOfRange(response, analyzedResponse.getBodyOffset(),response.length));
            if(bodyStr.contains("linkedin.com")){
                Document doc = Jsoup.parse(bodyStr);
                Elements anchors = doc.getElementsByTag("a");
                for (Element anchor : anchors) {
                    if( anchor.attr("href").contains("linkedin.com/in/")){
                        try {
                            StringJoiner joiner = new StringJoiner("\t");
                            URL href = new URL(anchor.attr("href"));
                            String host = href.getHost();
                            joiner.add(host);
                            //Ignore the Pipe Symbol and everything after it
                            String fullString = anchor.text().split("\\|")[0];
                            String[] splitOnDash = fullString.split(" - ");
                            for(int i=0; i < splitOnDash.length;i++){
                                if(i==0){
                                    String fullName = splitOnDash[i].trim();
                                    //ignore everything after a comma, like CPA, MD, etc
                                    String trimmedName = fullName.split(",")[0];
                                    String[] nameArray = trimmedName.split("\\s+");
                                    //allow up to four names, joining all remaining names into the last name
                                    for(int j=0; j < nameArray.length ;j++){
                                        if(j<3){
                                            joiner.add(nameArray[j]);
                                        }
                                        else if(j==3){ //name array has 4 or more strings, combine all remaining names as the last name
                                            joiner.add(String.join(" ", Arrays.copyOfRange(nameArray, 3, nameArray.length)));
                                        }
                                    }
                                    //if the names array had less than 4 elements, put some fillers in to keep columns aligned when importing into Excel
                                    for(int k=nameArray.length;k<4;k++){
                                        joiner.add("");
                                    }
                                }
                                else{
                                    joiner.add(splitOnDash[i].trim());
                                }
                                
                            }
                            //Output header row
                            if(dupeList.isEmpty()){
                                StringJoiner joiner2 = new StringJoiner("\t");
                                joiner2.add("source").add("Name 1").add("Name 2").add("Name 3").add("Name 4").add("Description 1").add("Description 2").add("Description 3").add("Description 4");
                                stdout.println(joiner2.toString());
                            }
                            //Only output this line if it hasn't been output before
                            if(!dupeList.contains(joiner.toString())){
                                dupeList.add(joiner.toString());
                                stdout.println(joiner.toString());
                            }
                            
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(BurpExtender.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
            }
        }
    }

    public IBurpExtenderCallbacks getCallbacks()
    {
        return this.callbacks;
    }
}