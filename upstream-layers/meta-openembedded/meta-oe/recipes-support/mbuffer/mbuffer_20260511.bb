DESCRIPTION = "mbuffer is a tool for buffering data streams with a large set of unique features."
HOMEPAGE = "http://www.maier-komor.de/mbuffer.html"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"
SECTION = "console/network"

SRC_URI = " \
    http://www.maier-komor.de/software/mbuffer/mbuffer-${PV}.tgz \
"

UPSTREAM_CHECK_URI = "http://www.maier-komor.de/mbuffer.html"

SRC_URI[sha256sum] = "13bab36f39408f7a08fb368913290ad0f117c934bab602094e18fcc123ec5783"

inherit autotools

# mbuffer's Makefile generates version.h by invoking "bash mkversion.sh"
# with no path, which only works when building in the source tree (the
# script is not found via VPATH in a separate build dir).
B = "${S}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[md5] = "--enable-md5,--disable-md5,openssl"

do_configure() {
    ( cd ${S}; gnu-configize )
    oe_runconf
}

