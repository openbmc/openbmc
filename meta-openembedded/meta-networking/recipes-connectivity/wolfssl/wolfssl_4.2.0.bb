SUMMARY = "wolfSSL Lightweight Embedded SSL/TLS Library"
DESCRIPTION = "wolfSSL, formerly CyaSSL, is a lightweight SSL library written \
               in C and optimized for embedded and RTOS environments. It can \
               be up to 20 times smaller than OpenSSL while still supporting \
               a full TLS client and server, up to TLS 1.3"
HOMEPAGE = "https://www.wolfssl.com/products/wolfssl"
BUGTRACKER = "https://github.com/wolfssl/wolfssl/issues"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PROVIDES += "cyassl"
RPROVIDES_${PN} = "cyassl"

SRC_URI = "git://github.com/wolfSSL/wolfssl.git;protocol=https"
SRCREV = "cb7b4af9861ad627faffdc3d24a196c2d11930b8"
S = "${WORKDIR}/git"

inherit autotools

BBCLASSEXTEND += "native nativesdk"
