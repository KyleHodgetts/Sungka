package com.lynx.sungka.server.http.route;

import com.lynx.sungka.server.ServerContext;
import com.lynx.sungka.server.http.RequestResponse;
import com.lynx.sungka.server.http.header.ContentLength;
import com.lynx.sungka.server.http.header.ContentType;
import com.lynx.sungka.server.util.Tuple2;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import java.io.IOException;
import java.util.List;

/**
 * @author Adam Chlupacek
 * @version 1.0
 * Part of routing on the server.
 * This denotes an end point for handling a request, must be at the end of each route.
 */
public abstract class Bind implements Route {

    /**
     * An end point to a call to the server, the response is build from the resources on the server and
     * the arguments given in the path
     * @param context   The server context, access to server resources
     * @param body      The body of the request
     * @param args      Arguments passed in path
     * @return          The server response to the request
     */
    public abstract RequestResponse run(ServerContext context,DBObject body,List<String> args);

    @Override
    public Tuple2<RequestResponse, ParseState> matchRequest(ParseState state, ServerContext ctx, int idx) {
        DBObject object = new BasicDBObject();
        //Parses the content of the incoming request, if the Content headers are set
        ContentType contentType = (ContentType)state.getRequest().getHeaders().get("Content-Type");
        ContentLength contentLength = (ContentLength)state.getRequest().getHeaders().get("Content-Length");
        if (contentType!=null && contentLength != null &&contentType.isJson()){
            try {
                char[] chars = new char[contentLength.getLength()];
                //Reads from the stream
                state.getBody().read(chars, 0, contentLength.getLength());
                //Parser parses the json into string, int, or json object, as of now we handle on the object type
                Object json = JSON.parse(new String(chars));
                if (json instanceof DBObject){
                    object = (DBObject) json;
                }
            } catch (IOException e) {
               //TODO decide how to handle this error, probs something like invalid request?
            }
        }
        RequestResponse response = null;
        if (idx >= state.getPath().length)
            response = run(ctx, object, state.getArgs());
        return new Tuple2<>(response,state);
    }
}
