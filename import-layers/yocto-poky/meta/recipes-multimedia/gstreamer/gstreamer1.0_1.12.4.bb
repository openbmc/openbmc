require gstreamer1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6762ed442b3822387a51c92d928ead0d \
                    file://gst/gst.h;beginline=1;endline=21;md5=e059138481205ee2c6fc1c079c016d0d"

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gstreamer/gstreamer-${PV}.tar.xz \
"
SRC_URI[md5sum] = "9d268f2e891cce1ac0832f1cc467d4ea"
SRC_URI[sha256sum] = "5a8704aa4c2eeb04da192c4a9942f94f860ac1a585de90d9f914bac26a970674"

S = "${WORKDIR}/gstreamer-${PV}"

CVE_PRODUCT = "gstreamer"
