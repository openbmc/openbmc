DESCRIPTION = "libmikmod is a module player library supporting many formats, including mod, s3m, it, and xm."
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "alsa-lib texinfo"

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/project/mikmod/${BPN}/${PV}/${BPN}-${PV}.tar.gz \
"
SRC_URI[md5sum] = "f69d7dd06d307e888f466fc27f4f680b"
SRC_URI[sha256sum] = "ad9d64dfc8f83684876419ea7cd4ff4a41d8bcd8c23ef37ecb3a200a16b46d19"

inherit autotools binconfig lib_package

EXTRA_OECONF = "\
    --disable-af \
    --enable-alsa \
    --disable-esd \
    --enable-oss \
    --disable-sam9407 \
    --disable-ultra \
    --disable-esdtest \
    --enable-threads \
"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'pulseaudio', d)}"
PACKAGECONFIG[pulseaudio] = "--enable-pulseaudio,--disable-pulseaudio,pulseaudio"
