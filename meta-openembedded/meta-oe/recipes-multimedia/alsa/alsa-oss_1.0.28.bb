SUMMARY = "Alsa OSS Compatibility Package"
SECTION = "libs/multimedia"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"
DEPENDS = "alsa-lib"

SRC_URI = "ftp://ftp.alsa-project.org/pub/oss-lib/alsa-oss-${PV}.tar.bz2 \
    file://libio.patch \
"
SRC_URI[md5sum] = "91f57e8cee1ad4cc956caa8b62ac5d43"
SRC_URI[sha256sum] = "3ae62caa88a0bc7b30ed836dcb794dc6ef4d3650439e2260db54cace7d5c6ad5"

inherit autotools

LEAD_SONAME = "libaoss.so.0"

do_configure_prepend () {
    touch NEWS README AUTHORS ChangeLog
    sed -i "s/libaoss.so/${LEAD_SONAME}/" ${S}/alsa/aoss.in
}
