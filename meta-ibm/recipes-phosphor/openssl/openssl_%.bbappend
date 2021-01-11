# Rainier needs ibmtpm2tss which needs ibmswtpm2 which needs openssl
# camellia support
EXTRA_OECONF_remove_witherspoon-tacoma = " no-camellia"
EXTRA_OECONF_remove_rainier = " no-camellia"
