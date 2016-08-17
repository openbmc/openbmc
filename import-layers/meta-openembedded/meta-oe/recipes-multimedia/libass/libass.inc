DESCRIPTION = "libass is a portable subtitle renderer for the ASS/SSA (Advanced Substation Alpha/Substation Alpha) subtitle format. It is mostly compatible with VSFilter."
HOMEPAGE = "http://code.google.com/p/libass/"
SECTION = "libs/multimedia"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ae98663bac55afe5d989919d296f28a"

DEPENDS = "enca fontconfig freetype libpng fribidi"

INC_PR = "r1"

SRC_URI = "http://${BPN}.googlecode.com/files/${BP}.tar.gz"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[harfbuzz] = "--enable-harfbuzz,--disable-harfbuzz,harfbuzz"

EXTRA_OECONF = " \
    --enable-enca \
    --enable-fontconfig \
"

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests = " \
    ${libdir}/test/test \
"
