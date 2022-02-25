SUMMARY = "XFont2: X Font rasterisation library"

DESCRIPTION = "libXfont2 provides various services for X servers, most \
notably font selection and rasterisation (through external libraries \
such as freetype)."

require xorg-lib-common.inc

LICENSE = "MIT & MIT & BSD-4-Clause & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=a46c8040f2f737bcd0c435feb2ab1c2c"

DEPENDS += "freetype xtrans xorgproto libfontenc zlib"

XORG_PN = "libXfont2"

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "aa7c6f211cf7215c0ab4819ed893dc98034363d7b930b844bb43603c2e10b53e"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

CVE_PRODUCT = "libxfont libxfont2"
