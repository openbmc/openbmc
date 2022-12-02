KEY_SIGN ?= "openssl"

KEY_BB_INDEX ?= "1"
KEY_BL31_INDEX ?= "1"
KEY_OPTEE_INDEX ?= "1"
KEY_UBOOT_INDEX ?= "1"

KEY_BB_ID ?= "11"
KEY_BL31_ID ?= "11"
KEY_OPTEE_ID ?= "11"
KEY_UBOOT_ID ?= "11"

KEY_BB ?= "${HOME}/keys/openssl/skmt_ecc_key_1.der"
KEY_BL31 ?= "${HOME}/keys/openssl/skmt_ecc_key_1.der"
KEY_OPTEE ?= "${HOME}/keys/openssl/skmt_ecc_key_1.der"
KEY_UBOOT ?= "${HOME}/keys/openssl/skmt_ecc_key_1.der"
