DESCRIPTION = "libmikmod is a module player library supporting many formats, including mod, s3m, it, and xm."
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "alsa-lib texinfo pulseaudio"

SRC_URI = "\
    ${SOURCEFORGE_MIRROR}/project/mikmod/${BPN}/${PV}/${BPN}-${PV}.tar.gz \
"
SRC_URI[md5sum] = "9dd9bed30c6f7607a55480234606071b"
SRC_URI[sha256sum] = "3f363e03f7b1db75b9b6602841bbd440ed275a548e53545f980df8155de4d330"

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

