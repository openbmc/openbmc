FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
require xen.inc

SRC_URI = " \
    https://downloads.xenproject.org/release/xen/${PV}/xen-${PV}.tar.gz \
    file://fix-libxc-xc_dom_arm-missing-initialization.patch \
    "

SRC_URI[md5sum] = "f0a753637630f982dfbdb64121fd71e1"
SRC_URI[sha256sum] = "cade643fe3310d4d6f97d0c215c6fa323bc1130d7e64d7e2043ffaa73a96f33b"

S = "${WORKDIR}/xen-${PV}"
