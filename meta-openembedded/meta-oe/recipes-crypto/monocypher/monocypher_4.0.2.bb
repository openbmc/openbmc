SUMMARY = "Monocypher is an easy-to-use crypto library"
DESCRIPTION = "Monocypher is an easy to use, easy to deploy, \
auditable crypto library written in portable C. It approaches the size of TweetNaCl and the speed of libsodium."
HOMEPAGE = "https://monocypher.org/"
SECTION = "libs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENCE.md;md5=6a251155d943c8531e01a139f3fc531a"

SRC_URI = "git://github.com/LoupVaillant/Monocypher.git;protocol=https;branch=master"
SRCREV = "0d85f98c9d9b0227e42cf795cb527dff372b40a4"

S = "${WORKDIR}/git"

CFLAGS+="-pedantic -Wall -Wextra -O3"
EXTRA_OEMAKE = "'PREFIX=${prefix}' 'DESTDIR=${D}' 'CFLAGS=${CFLAGS}'"

do_compile() {
    oe_runmake library
}

do_install() {
    oe_runmake install-lib
    oe_runmake install-pc
}

BBCLASSEXTEND = "native nativesdk"
