include gstreamer1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6762ed442b3822387a51c92d928ead0d \
                    file://gst/gst.h;beginline=1;endline=21;md5=e059138481205ee2c6fc1c079c016d0d"

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gstreamer/gstreamer-${PV}.tar.xz \
    file://0001-Fix-crash-with-gst-inspect.patch \
    file://0001-gstinfo-Shorten-__FILE__-on-all-platforms.patch \
    file://inputselector-sticky-events-haven-t-send-out-when-ac-1-4-1.patch \
    file://0002-basesink-Fix-QoS-lateness-checking-if-subclass-imple.patch \
    file://0003-basesink-Shouldn-t-drop-buffer-when-sync-false.patch \
"
SRC_URI[md5sum] = "88a9289c64a4950ebb4f544980234289"
SRC_URI[sha256sum] = "40801aa7f979024526258a0e94707ba42b8ab6f7d2206e56adbc4433155cb0ae"

S = "${WORKDIR}/gstreamer-${PV}"

