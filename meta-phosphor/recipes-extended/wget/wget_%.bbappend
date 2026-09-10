PACKAGECONFIG:remove:openbmc-phosphor = "gnutls"
PACKAGECONFIG:append:openbmc-phosphor = " openssl"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
SRC_URI:append = " \
    file://0001-src-openssl.c-ssl_init-Fix-build-for-OpenSSL-4.0.0.patch \
    "
