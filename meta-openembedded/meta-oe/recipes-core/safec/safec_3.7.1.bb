SUMMARY  = "Safe C Library"

LICENSE  = "safec"
LIC_FILES_CHKSUM = "file://COPYING;md5=6d0eb7dfc57806a006fcbc4e389cf164"
SECTION = "lib"

inherit autotools pkgconfig

S = "${WORKDIR}/git"
SRCREV = "f9add9245b97c7bda6e28cceb0ee37fb7e254fd8"
SRC_URI = "git://github.com/rurban/safeclib.git;branch=master;protocol=https \
           file://0001-strpbrk_s-Remove-unused-variable-len.patch \
           "

COMPATIBLE_HOST = '(x86_64|i.86|powerpc|powerpc64|arm|aarch64|mips).*-linux'

PACKAGES =+ "${PN}-check"

FILES:${PN}-check += "${bindir}/check_for_unsafe_apis"

RDEPENDS:${PN}-check += "perl"
