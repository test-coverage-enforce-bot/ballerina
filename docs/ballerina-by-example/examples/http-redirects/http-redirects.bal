import ballerina/io;
import ballerina/net.http;
import ballerina/mime;

endpoint http:ClientEndpoint clientEP {
    targets:[{uri:"http://www.mocky.io"}],
    followRedirects : { enabled : true, maxCount : 5 }
};

function main (string[] args) {
    http:Request req = {};

    //Send a GET request to the specified endpoint
    var returnResult = clientEP -> get("/v2/59d590762700000a049cd694", req);
    match returnResult {
        http:HttpConnectorError connectorErr => {io:println("Connector error!");}
        http:Response resp => {
            match resp.getStringPayload() {
                http:PayloadError payloadError => {io:println(payloadError.message);}
                string payload => io:println("Response received for the GET request is : " + payload);
            }
        }
    }
}
