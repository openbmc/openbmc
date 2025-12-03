# IBM enterprise needs ibmtpm2tss which needs ibmswtpm2 which needs openssl
# camellia support
DEPRECATED_CRYPTO_FLAGS:remove:ibm-enterprise = " no-camellia"
