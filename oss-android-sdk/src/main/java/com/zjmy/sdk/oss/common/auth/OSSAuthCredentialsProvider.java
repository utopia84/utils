package com.zjmy.sdk.oss.common.auth;

import com.zjmy.sdk.oss.exception.ClientException;
import com.zjmy.sdk.oss.common.OSSConstants;
import com.zjmy.sdk.oss.common.utils.IOUtils;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jingdan on 2017/11/15.
 * Authentication server issued under the agreement of the official website agreement, you can directly use the provider
 */

public class OSSAuthCredentialsProvider extends OSSFederationCredentialProvider {

    private String mAuthServerUrl;
    private AuthDecoder mDecoder;

    public OSSAuthCredentialsProvider(String authServerUrl) {
        this.mAuthServerUrl = authServerUrl;
    }

    /**
     * set auth server url
     *
     * @param authServerUrl
     */
    public void setAuthServerUrl(String authServerUrl) {
        this.mAuthServerUrl = authServerUrl;
    }

    /**
     * set response data decoder
     *
     * @param decoder
     */
    public void setDecoder(AuthDecoder decoder) {
        this.mDecoder = decoder;
    }

    @Override
    public OSSFederationToken getFederationToken() throws ClientException {
        OSSFederationToken authToken;
        String authData;
        try {
            URL stsUrl = new URL(mAuthServerUrl);
            HttpURLConnection conn = (HttpURLConnection) stsUrl.openConnection();
            conn.setConnectTimeout(10000);
            InputStream input = conn.getInputStream();
            authData = IOUtils.readStreamAsString(input, OSSConstants.DEFAULT_CHARSET_NAME);
            if (mDecoder != null) {
                authData = mDecoder.decode(authData);
            }
            JSONObject jsonObj = new JSONObject(authData);
            int statusCode = jsonObj.getInt("statusCode");
            if (statusCode == 200) {
                String ak = jsonObj.getString("accessKeyId");
                String sk = jsonObj.getString("accessKeySecret");
                String token = jsonObj.getString("securityToken");
                String expiration = jsonObj.getString("expiration");
                authToken = new OSSFederationToken(ak, sk, token, expiration);
            } else {
                String errorCode = jsonObj.getString("errorCode");
                String errorMessage = jsonObj.getString("errorMessage");
                throw new ClientException("errorCode: " + errorCode + "| errorMessage: " + errorMessage);
            }
            return authToken;
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    public interface AuthDecoder {
        String decode(String data);
    }
}
