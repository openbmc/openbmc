include gstreamer1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6762ed442b3822387a51c92d928ead0d \
                    file://gst/gst.h;beginline=1;endline=21;md5=e059138481205ee2c6fc1c079c016d0d"

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gstreamer/gstreamer-${PV}.tar.xz \
"

SRC_URI[md5sum] = "e88dad542df9d986822e982105d2b530"
SRC_URI[sha256sum] = "66b37762d4fdcd63bce5a2bec57e055f92420e95037361609900278c0db7c53f"

S = "${WORKDIR}/gstreamer-${PV}"
