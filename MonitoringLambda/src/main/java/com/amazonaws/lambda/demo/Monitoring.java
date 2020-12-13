package com.amazonaws.lambda.demo;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Monitoring implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);
        String json = ""+input;
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        JsonElement state = element.getAsJsonObject().get("state");
        JsonElement reported = state.getAsJsonObject().get("reported");
        String dustdensity = reported.getAsJsonObject().get("dustdensity").getAsString();
        double dust = Double.valueOf(dustdensity);

        final String AccessKey="";
        final String SecretKey="";
        final String topicArn="";

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AccessKey, SecretKey);  
        AmazonSNS sns = AmazonSNSClientBuilder.standard()
                    .withRegion(Regions.AP_NORTHEAST_2)
                    .withCredentials( new AWSStaticCredentialsProvider(awsCreds) )
                    .build();

        final String msg = "*Dustdensity Warning!*\n" + "Your place dust is " + dust ;
        final String subject = "Critical Warning";
        if (dust >= 110.0) {
            PublishRequest publishRequest = new PublishRequest(topicArn, msg, subject);
            PublishResult publishResponse = sns.publish(publishRequest);
        }

        return subject+ "dustdensity = " + dustdensity + "!";
    }

}
