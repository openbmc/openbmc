SUMMARY = "XFont2: X Font rasterisation library"

DESCRIPTION = "libXfont2 provides various services for X servers, most \
notably font selection and rasterisation (through external libraries \
such as freetype)."

require xorg-lib-common.inc

LICENSE = "MIT & MIT-style & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=a46c8040f2f737bcd0c435feb2ab1c2c"

DEPENDS += "freetype xtrans fontsproto libfontenc zlib"

XORG_PN = "libXfont2"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "b7ca87dfafeb5205b28a1e91ac3efe85"
SRC_URI[sha256sum] = "0e8ab7fd737ccdfe87e1f02b55f221f0bd4503a1c5f28be4ed6a54586bac9c4e"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
