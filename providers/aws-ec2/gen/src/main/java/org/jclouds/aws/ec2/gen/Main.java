package org.jclouds.aws.ec2.gen;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/** Entry point for the code generator */
public class Main {

    protected static final String METADATA_URL = "https://pricing.us-east-1.amazonaws.com/offers/v1.0/aws/AmazonEC2/current/index.json";
    protected static final String DEFAULT_SOURCE_PATH = "../src/main/java";
    
    public static final String USAGE =
        "Arguments:\n\n" +
        " --help - Show this.\n" +
        " --src DIR - Optional directory to generate source into. The com.softlayer.api.service package\n" +
        "   underneath this directory will be cleaned before code is generated. If not given,\n" +
        "   ../src/main/java is used\n" +
        " --url URL - Optional metadata URL. If not given, http://api.softlayer.com/metadata is used.\n" +
        " --whitelist FILENAME - Optional set of types, properties, and methods to whitelist. It is one\n" +
        "   entry per line and anything not entered will not be included in the generated client. Simply\n" +
        "   give the type name, the property as type_name.propertyName, or the method as type_name::methodName.\n" +
        "   This is mutually exclusive with --blacklist.\n" +
        " --blacklist FILENAME - Similar to, and mututally exclusive with, --whitelist. Anything included\n" +
        "   here will NOT be generated.\n";

    public static void main(String[] args) throws Exception {
        // Load up args
        File dir;
        URL url;
        Restriction whitelist;
        Restriction blacklist;
        try {
            List<String> argList = Arrays.asList(args);
            if (argList.contains("--help")) {
                System.out.println(USAGE);
                return;
            }
            String dirString = getArg("--src", argList);
            dir = new File(dirString != null ? dirString : DEFAULT_SOURCE_PATH);
            String urlString = getArg("--url", argList);
            url = new URL(urlString != null ? urlString : METADATA_URL);
//            whitelist = getRestriction(getArg("--whitelist", argList));
//            blacklist = getRestriction(getArg("--blacklist", argList));
//            if (whitelist != null && blacklist != null) {
//                throw new IllegalArgumentException("Can't have whitelist and blacklist");
//            }
        } catch (Exception e) {
            System.out.println(USAGE);
            throw e;
        }
        
        new Generator(dir, url, null, null).buildClient();
    }
    
    private static String getArg(String argName, List<String> argList) {
        int index = argList.indexOf(argName);
        if (index == -1) {
            return null;
        } else if (argList.size() == index + 1) {
            throw new IllegalArgumentException("No value found for argument " + argName);
        } else {
            argList.remove(index);
            return argList.remove(index);
        }
    }

}
