DESCRIPTION = "libass is a portable subtitle renderer for the ASS/SSA (Advanced Substation Alpha/Substation Alpha) subtitle format. It is mostly compatible with VSFilter."
HOMEPAGE = "https://github.com/libass/libass"
SECTION = "libs/multimedia"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=a42532a0684420bdb15556c3cdd49a75"

DEPENDS = "enca fontconfig freetype libpng fribidi"

SRC_URI = "https://github.com/libass/libass/releases/download/${PV}/libass-${PV}.tar.xz"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[harfbuzz] = "--enable-harfbuzz,--disable-harfbuzz,harfbuzz"

EXTRA_OECONF = " \
    --enable-fontconfig \
"

# Disable compiling with ASM for x86 to avoid textrel
EXTRA_OECONF_append_x86 = " --disable-asm"

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests = " \
    ${libdir}/test/test \
"
