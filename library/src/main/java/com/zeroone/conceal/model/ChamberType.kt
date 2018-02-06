package com.zeroone.conceal.model

import com.facebook.crypto.CryptoConfig

/**
 * Created by hafiq on 28/01/2018.
 */

enum class ChamberType private constructor(val config: CryptoConfig) {

    KEY_128(CryptoConfig.KEY_128),
    KEY_256(CryptoConfig.KEY_256)
}
