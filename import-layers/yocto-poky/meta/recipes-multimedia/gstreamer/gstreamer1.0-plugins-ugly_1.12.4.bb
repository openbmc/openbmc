require gstreamer1.0-plugins-ugly.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://tests/check/elements/xingmux.c;beginline=1;endline=21;md5=4c771b8af188724855cb99cadd390068"

SRC_URI = " \
    http://gstreamer.freedesktop.org/src/gst-plugins-ugly/gst-plugins-ugly-${PV}.tar.xz \
    file://0001-introspection.m4-prefix-pkgconfig-paths-with-PKG_CON.patch \
"
SRC_URI[md5sum] = "e2b836fb2747f6ae3a1a6f33a9d8c952"
SRC_URI[sha256sum] = "1c165b8d888ed350acd8e6ac9f6fe06508e6fcc0a3afc6ccc9fbeb30df9be522"

S = "${WORKDIR}/gst-plugins-ugly-${PV}"
