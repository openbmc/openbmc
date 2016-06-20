include gstreamer1.0.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=6762ed442b3822387a51c92d928ead0d \
                    file://gst/gst.h;beginline=1;endline=21;md5=e059138481205ee2c6fc1c079c016d0d"

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gstreamer/gstreamer-${PV}.tar.xz \
    file://0001-Fix-crash-with-gst-inspect.patch \
"

SRC_URI[md5sum] = "b4cdeb2b9cb20dd6ac022a4f417eae0d"
SRC_URI[sha256sum] = "22f9568d67b87cf700a111f381144bd37cb93790a77e4e331db01fe854a37f24"

S = "${WORKDIR}/gstreamer-${PV}"
