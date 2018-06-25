/* writed by Daiyuxian  for support traverse the metrics , 2018*/

import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.OpenShiftConfigBuilder;
import io.fabric8.kubernetes.client.internal.KubeConfigUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileReader; 
import java.io.FileNotFoundException;  
import java.io.UnsupportedEncodingException;  

public class openshiftMetrics {
    public static String  getTokenString(String tokenFile) {  
        String encoding = "UTF-8";  
        File file = new File(tokenFile);  
        Long filelength = file.length();  
        byte[] filecontent = new byte[filelength.intValue()];  
        
        try {  
            FileInputStream in = new FileInputStream(file);  
            in.read(filecontent);  
            in.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        try {  
            return new String(filecontent, encoding); 
        } catch (UnsupportedEncodingException e) {  
            System.err.println("The OS does not support " + encoding);  
            e.printStackTrace();  
            return null;  
        }  
    }
  public static void main(String[] args) {
    System.out.println("DAi show the project information:");
    OpenShiftConfig config = new OpenShiftConfigBuilder()
    .withOpenShiftUrl("https://masterdnsk3vubochvtlpa.westus.cloudapp.azure.com")
    .withMasterUrl("https://masterdnsk3vubochvtlpa.westus.cloudapp.azure.com")
    .withUsername("sentience")
    .withPassword("HON123wellwell")
    .withTrustCerts(true).build();

    OpenShiftClient client = new DefaultOpenShiftClient(config);
    System.out.println("User:sentience");
    System.out.println("URL:https:/masterdnsk3vubochvtlpa.westus.cloudapp.azure.com");
    NamespaceList myNs = client.namespaces().list();
    for(Namespace ns: myNs.getItems()){
        System.out.println(ns.getMetadata().getName());
    }
    String token = getTokenString("/var/run/secrets/kubernetes.io/serviceaccount/token");
    token = token.trim();
    
   ProcessBuilder pb = new ProcessBuilder("curl",
        "-k",
        "-H",
        "Authorization: Bearer "+ token,
        "-H",
        "Hawkular-Tenant: openshift-infra",
        "-X",
        "GET",
        "https://hawkular-metrics.104.42.248.59.nip.io/hawkular/metrics/metrics"
        );

    pb.redirectErrorStream(true);
    Process p;
    try {
        p = pb.start();
        BufferedReader br = null;
        String line = null;
        br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while((line = br.readLine())!= null){
            System.out.println("\t" + line);
        }
        br.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }
}

