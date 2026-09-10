SUMMARY = "XFont2: X Font rasterisation library"

DESCRIPTION = "libXfont2 provides various services for X servers, most \
notably font selection and rasterisation (through external libraries \
such as freetype)."

require xorg-lib-common.inc

LICENSE = "BSD-2-Clause AND BSD-4-Clause-UC AND MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=a5d1388c9d40be70dbad35fa440443f7"

DEPENDS += "freetype xtrans xorgproto libfontenc zlib"

XORG_PN = "libXfont2"

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "f042a370666815e7b941e9b7019024755bd1c6c2954afbfa515af378251799e2"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

CVE_PRODUCT = "libxfont libxfont2"
