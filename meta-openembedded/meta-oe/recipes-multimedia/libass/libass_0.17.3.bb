DESCRIPTION = "libass is a portable subtitle renderer for the ASS/SSA (Advanced Substation Alpha/Substation Alpha) subtitle format. It is mostly compatible with VSFilter."
HOMEPAGE = "https://github.com/libass/libass"
SECTION = "libs/multimedia"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=a42532a0684420bdb15556c3cdd49a75"

DEPENDS = "fontconfig freetype fribidi harfbuzz"

SRC_URI = "git://github.com/libass/libass.git;protocol=https;branch=master"
SRCREV = "e46aedea0a0d17da4c4ef49d84b94a7994664ab5"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGECONFIG[asm] = "--enable-asm,--disable-asm,nasm-native"
# use larger tiles in the rasterizer (better performance, slightly worse quality)
PACKAGECONFIG[largetiles] = "--enable-large-tiles,--disable-large-tiles"

PACKAGECONFIG ??= ""
PACKAGECONFIG:append:x86-64 = " asm"

PACKAGES =+ "${PN}-tests"

FILES:${PN}-tests = " \
    ${libdir}/test/test \
"
