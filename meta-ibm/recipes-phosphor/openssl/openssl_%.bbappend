# P10 needs ibmtpm2tss which needs ibmswtpm2 which needs openssl
# camellia support
DEPRECATED_CRYPTO_FLAGS:remove:p10bmc = " no-camellia"
