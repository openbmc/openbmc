SUMMARY = "Alsa OSS Compatibility Package"
SECTION = "libs/multimedia"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ed055b4eff93da784176a01582e6ec1a"
DEPENDS = "alsa-lib"

SRC_URI = "https://www.alsa-project.org/files/pub/oss-lib/alsa-oss-${PV}.tar.bz2 \
"
SRC_URI[md5sum] = "9ec4bb783fdce19032aace086d65d874"
SRC_URI[sha256sum] = "64adcef5927e848d2e024e64c4bf85b6f395964d9974ec61905ae4cb8d35d68e"

inherit autotools

LEAD_SONAME = "libaoss.so.0"

do_configure_prepend () {
    touch NEWS README AUTHORS ChangeLog
    sed -i "s/libaoss.so/${LEAD_SONAME}/" ${S}/alsa/aoss.in
}

# http://errors.yoctoproject.org/Errors/Details/186961/
COMPATIBLE_HOST_libc-musl = 'null'
