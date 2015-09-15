include gstreamer1.0-plugins-ugly.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://tests/check/elements/xingmux.c;beginline=1;endline=21;md5=4c771b8af188724855cb99cadd390068 "

SRC_URI[md5sum] = "6954beed7bb9a93e426dee543ff46393"
SRC_URI[sha256sum] = "5cd5e81cf618944f4dc935f1669b2125e8bb2fe9cc7dc8dc15b72237aca49067"

S = "${WORKDIR}/gst-plugins-ugly-${PV}"

