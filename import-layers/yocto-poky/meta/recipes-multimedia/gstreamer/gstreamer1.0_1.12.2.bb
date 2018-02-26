require gstreamer1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6762ed442b3822387a51c92d928ead0d \
                    file://gst/gst.h;beginline=1;endline=21;md5=e059138481205ee2c6fc1c079c016d0d"

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gstreamer/gstreamer-${PV}.tar.xz \
    file://0001-configure-Add-switches-for-enabling-disabling-libdw-.patch \
"
SRC_URI[md5sum] = "4748860621607ffd96244fb79c86c238"
SRC_URI[sha256sum] = "9fde3f39a2ea984f9e07ce09250285ce91f6e3619d186889f75b5154ecf994ba"

S = "${WORKDIR}/gstreamer-${PV}"
