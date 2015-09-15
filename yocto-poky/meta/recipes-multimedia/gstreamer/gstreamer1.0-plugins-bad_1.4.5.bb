include gstreamer1.0-plugins-bad.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=73a5855a8119deb017f5f13cf327095d \
                    file://gst/tta/filters.h;beginline=12;endline=29;md5=8a08270656f2f8ad7bb3655b83138e5a \
                    file://COPYING.LIB;md5=21682e4e8fea52413fd26c60acb907e5 \
                    file://gst/tta/crc32.h;beginline=12;endline=29;md5=27db269c575d1e5317fffca2d33b3b50"

SRC_URI += "file://0001-gl-do-not-check-for-GL-GLU-EGL-GLES2-libs-if-disable.patch"

SRC_URI[md5sum] = "e0bb39412cf4a48fe0397bcf3a7cd451"
SRC_URI[sha256sum] = "152fad7250683d72f9deb36c5685428338365fe4a4c87ffe15e38783b14f983c"

S = "${WORKDIR}/gst-plugins-bad-${PV}"

