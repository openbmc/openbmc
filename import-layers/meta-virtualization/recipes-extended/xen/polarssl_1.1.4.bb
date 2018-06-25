# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "PolarSSL (now 'mbed TLS') is an open source, portable, easy to use, readable and flexible SSL library."
HOMEPAGE = "https://tls.mbed.org"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=751419260aa954499f7abaabaa882bbe"

# git hash for release tag polarssl-1.1.4
SRCREV_polarssl = "d36da11125a9c85c572a4fdf63e0a25e76d7bb18"
SRC_URI = "\
    git://github.com/ARMmbed/mbedtls.git;protocol=https;nobranch=1;destsuffix=polarssl;name=polarssl \
    file://polarssl.patch; \
"

S="${WORKDIR}/${PN}"
B="${S}/library"

require polarssl.inc
