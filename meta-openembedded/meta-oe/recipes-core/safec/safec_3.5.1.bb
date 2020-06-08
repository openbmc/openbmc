SUMMARY  = "Safe C Library"

LICENSE  = "safec"
LIC_FILES_CHKSUM = "file://COPYING;md5=6d0eb7dfc57806a006fcbc4e389cf164"
SECTION = "lib"

inherit autotools pkgconfig

S = "${WORKDIR}/git"
# v08112019
SRCREV = "ad76c7b1dbd0403b0c9decf54164fcce271c590f"
SRC_URI = "git://github.com/rurban/safeclib.git \
"

COMPATIBLE_HOST = '(x86_64|i.86|powerpc|powerpc64|arm|aarch64|mips).*-linux'

PACKAGES =+ "${PN}-check"

FILES_${PN}-check += "${bindir}/check_for_unsafe_apis"

RDEPENDS_${PN}-check += "perl"
