package com.kuantik.validation.infrastructure.blacklist;

import com.kuantik.validation.infrastructure.blacklist.dto.BlacklistRfcDetailResponse;
import com.kuantik.validation.infrastructure.blacklist.dto.BlacklistRfcExistsResponse;

public interface BlacklistClient {

    BlacklistRfcExistsResponse verifyRfcExistence(String rfc);

    BlacklistRfcDetailResponse verifyRfcDetail(String rfc);
}
