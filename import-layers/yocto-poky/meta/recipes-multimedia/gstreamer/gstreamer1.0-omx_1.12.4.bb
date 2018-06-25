include gstreamer1.0-omx.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c \
                    file://omx/gstomx.h;beginline=1;endline=21;md5=5c8e1fca32704488e76d2ba9ddfa935f"

SRC_URI = "http://gstreamer.freedesktop.org/src/gst-omx/gst-omx-${PV}.tar.xz"

SRC_URI[md5sum] = "eb8d5ae3b69cfeed9dc77c592106247e"
SRC_URI[sha256sum] = "a025fa24242ec868fe0ff1e66d806a1070bcbc7c14a987a89cdc3395d0d56d5f"

S = "${WORKDIR}/gst-omx-${PV}"
