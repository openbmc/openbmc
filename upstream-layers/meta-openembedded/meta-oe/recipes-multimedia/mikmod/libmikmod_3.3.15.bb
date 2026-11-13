DESCRIPTION = "libmikmod is a module player library supporting many formats, including mod, s3m, it, and xm."
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=4bf661c1e3793e55c8d1051bc5e0ae21"

DEPENDS = "alsa-lib texinfo"

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/project/mikmod/${BPN}/${PV}/${BPN}-${PV}.tar.gz \
"
SRC_URI[sha256sum] = "dc27b338154b8f88dc9e6317196d42c6abc13bf63c4e055257a18d4e38e1afa2"
UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/mikmod/files/libmikmod/"
UPSTREAM_CHECK_REGEX = "/(?P<pver>\d+(\.\d+)+)/"

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
