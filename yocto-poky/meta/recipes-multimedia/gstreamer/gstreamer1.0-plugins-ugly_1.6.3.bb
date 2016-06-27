include gstreamer1.0-plugins-ugly.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://tests/check/elements/xingmux.c;beginline=1;endline=21;md5=4c771b8af188724855cb99cadd390068"

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gst-plugins-ugly/gst-plugins-ugly-${PV}.tar.xz \
"

SRC_URI[md5sum] = "dbd92afb3816cbfa90ab1f197144a2e2"
SRC_URI[sha256sum] = "2fecf7b7c7882f8f62f1900048f4013f98c214fb3d3303d8d812245bb41fd064"

S = "${WORKDIR}/gst-plugins-ugly-${PV}"
