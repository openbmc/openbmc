DESCRIPTION = "mbuffer is a tool for buffering data streams with a large set of unique features."
HOMEPAGE = "http://www.maier-komor.de/mbuffer.html"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"
SECTION = "console/network"

SRC_URI = " \
    http://www.maier-komor.de/software/mbuffer/mbuffer-20140310.tgz \
"

UPSTREAM_CHECK_URI = "http://www.maier-komor.de/mbuffer.html"

SRC_URI[sha256sum] = "ab90b6fdce16db2bf08bcda0bc5d2bfb053a9a163d2a24f95fbf246460549b99"

inherit autotools

PACKAGECONFIG ??= ""
PACKAGECONFIG[md5] = "--enable-md5,--disable-md5,openssl"

do_configure() {
    ( cd ${S}; gnu-configize )
    oe_runconf
}

