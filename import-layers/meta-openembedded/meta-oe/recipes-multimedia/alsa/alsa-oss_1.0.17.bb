SUMMARY = "Alsa OSS Compatibility Package"
SECTION = "libs/multimedia"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"
DEPENDS = "alsa-lib"
PR = "r1"

SRC_URI = "ftp://ftp.alsa-project.org/pub/oss-lib/alsa-oss-${PV}.tar.bz2 \
    file://libio.patch \
"

inherit autotools

LEAD_SONAME = "libaoss.so.0"

do_configure_prepend () {
    touch NEWS README AUTHORS ChangeLog
    sed -i "s/libaoss.so/${LEAD_SONAME}/" ${S}/alsa/aoss.in
}

SRC_URI[md5sum] = "1b1850c2fc91476a73d50f537cbd402f"
SRC_URI[sha256sum] = "8d009e23e2cbee1691ec3c95d1838056a804d98440eae7715d6c3aebc710f9ca"
