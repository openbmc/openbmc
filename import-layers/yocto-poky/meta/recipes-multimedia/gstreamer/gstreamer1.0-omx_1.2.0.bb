include gstreamer1.0-omx.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://omx/gstomx.h;beginline=1;endline=21;md5=5c8e1fca32704488e76d2ba9ddfa935f"

SRC_URI = "http://gstreamer.freedesktop.org/src/gst-omx/gst-omx-${PV}.tar.xz"

SRC_URI[md5sum] = "d24e8c0153c35dfefee3e26b1c2c35f8"
SRC_URI[sha256sum] = "0b4874961e6488ad9e5808114bd486ea981c540907262caab1419355fd82d745"

S = "${WORKDIR}/gst-omx-${PV}"
