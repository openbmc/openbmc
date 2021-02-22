# Rainier needs ibmtpm2tss which needs ibmswtpm2 which needs openssl
# camellia support
DEPRECATED_CRYPTO_FLAGS_remove_witherspoon-tacoma = " no-camellia"
DEPRECATED_CRYPTO_FLAGS_remove_rainier = " no-camellia"
