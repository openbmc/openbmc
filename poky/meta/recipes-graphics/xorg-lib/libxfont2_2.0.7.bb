SUMMARY = "XFont2: X Font rasterisation library"

DESCRIPTION = "libXfont2 provides various services for X servers, most \
notably font selection and rasterisation (through external libraries \
such as freetype)."

require xorg-lib-common.inc

LICENSE = "MIT & MIT & BSD-4-Clause-UC & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=a5d1388c9d40be70dbad35fa440443f7"

DEPENDS += "freetype xtrans xorgproto libfontenc zlib"

XORG_PN = "libXfont2"

BBCLASSEXTEND = "native"

SRC_URI[sha256sum] = "8b7b82fdeba48769b69433e8e3fbb984a5f6bf368b0d5f47abeec49de3e58efb"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

CVE_PRODUCT = "libxfont libxfont2"
