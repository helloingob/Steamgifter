package com.helloingob.gifter.data.to;

import com.helloingob.gifter.to.UserAssetTO;
import com.helloingob.gifter.to.UserTO;

public class AdvUserTO extends UserTO {

    private String userAgent;
    private String xrefToken;
    private UserAssetTO userAsset;

    public AdvUserTO() {
        super();
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public UserAssetTO getUserAsset() {
        return userAsset;
    }

    public void setUserAsset(UserAssetTO userAsset) {
        this.userAsset = userAsset;
    }

    public String getXrefToken() {
        return xrefToken;
    }

    public void setXrefToken(String xrefToken) {
        this.xrefToken = xrefToken;
    }
    
}
