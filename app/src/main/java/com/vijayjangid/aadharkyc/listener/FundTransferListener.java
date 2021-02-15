package com.vijayjangid.aadharkyc.listener;

import com.vijayjangid.aadharkyc.model.FundTransfer;

public interface FundTransferListener {
    void onFundTransfer(FundTransfer fundTransfer, int type);
}
