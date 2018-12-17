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

S = "${WORKDIR}/git"
SRCREV = "1196a3b64d9fabffc8273b87f6f69ac0e75d2eb7"
SRC_URI = "git://github.com/wolfSSL/wolfssl.git;protocol=https; \
           file://0001-fix-no-rule-to-make-cyassl-options.h.patch \
          "

inherit autotools

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
