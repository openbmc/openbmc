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

SRC_URI = "https://www.wolfssl.com/wolfssl-${PV}.zip"
SRC_URI[md5sum] = "6ec08c09e3f51cccbb1686b4fd45f92f"
SRC_URI[sha256sum] = "4cc318c49580d3b9c361fe258fac6106624aa744f1d34e03977b587766a753ee"

inherit autotools

BBCLASSEXTEND += "native nativesdk"
