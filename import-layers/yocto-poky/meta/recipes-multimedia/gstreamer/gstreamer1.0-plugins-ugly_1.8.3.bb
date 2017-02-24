include gstreamer1.0-plugins-ugly.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://tests/check/elements/xingmux.c;beginline=1;endline=21;md5=4c771b8af188724855cb99cadd390068"

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gst-plugins-ugly/gst-plugins-ugly-${PV}.tar.xz \
"

SRC_URI[md5sum] = "4fc66c77253b0ad5ce224bda654b2e7d"
SRC_URI[sha256sum] = "6fa2599fdd072d31fbaf50c34af406e2be944a010b1f4eab67a5fe32a0310693"

S = "${WORKDIR}/gst-plugins-ugly-${PV}"
