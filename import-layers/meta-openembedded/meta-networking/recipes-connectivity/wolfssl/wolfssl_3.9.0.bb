SUMMARY = "wolfSSL Lightweight, Embedded SSL Library"
DESCRIPTION = "wolfSSL, formerly CyaSSL, is a lightweight SSL library written in C and \
               optimized for embedded and RTOS environments. It can be \
               Up to 20 times smaller than OpenSSL while still supporting \
               a full TLS 1.2 client and server."
HOMEPAGE = "http://www.wolfssl.com/yaSSL/Products-wolfssl.html"
BUGTRACKER = "http://github.com/wolfssl/wolfssl/issues"
SECTION = "libs"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

PROVIDES += "cyassl"
RPROVIDES_${PN} = "cyassl"

SRC_URI = "http://www.wolfssl.com/${BP}.zip"

SRC_URI[md5sum] = "f3396726a9befd61443c2cce216e39ba"
SRC_URI[sha256sum] = "98f50244f7b43f8683bd0cf5c599849d330e75e6cf077e96f14e83bda8b03ca3"

inherit autotools

