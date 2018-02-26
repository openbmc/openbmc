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

SRC_URI = "https://github.com/wolfSSL/wolfssl/archive/v${PV}-stable.zip \
           file://0001-fix-no-rule-to-make-cyassl-options.h.patch \
          "

SRC_URI[md5sum] = "f9caf558169ab650dc8200708799ebeb"
SRC_URI[sha256sum] = "b6d87d3e2c8757177d69aff373c91cf162f4a3944fae90fa10d086fd5f9542e7"

S = "${WORKDIR}/wolfssl-${PV}-stable"

inherit autotools

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
