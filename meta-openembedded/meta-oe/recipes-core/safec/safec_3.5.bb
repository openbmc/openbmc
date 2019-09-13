SUMMARY  = "Safe C Library"

LICENSE  = "safec"
LIC_FILES_CHKSUM = "file://COPYING;md5=6d0eb7dfc57806a006fcbc4e389cf164"
SECTION = "lib"

inherit autotools pkgconfig

S = "${WORKDIR}/git"
SRCREV = "a99a052a56da409638c9fe7e096a5ae6661ca7cb"
SRC_URI = "git://github.com/rurban/safeclib.git \
           file://0001-memrchr-Use-_ISOC11_SOURCE-only-with-glibc.patch \
"

CPPFLAGS_append_libc-musl = " -D_GNU_SOURCE"

COMPATIBLE_HOST = '(x86_64|i.86|powerpc|powerpc64|arm).*-linux'

RDEPENDS_${PN} = "perl"
