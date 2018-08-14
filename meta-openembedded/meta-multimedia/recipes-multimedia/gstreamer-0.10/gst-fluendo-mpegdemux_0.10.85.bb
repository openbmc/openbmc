require gst-fluendo.inc

SUMMARY = "Fluendo MPEG Transport Stream and Program Stream demuxer for GStreamer"
LICENSE = "MPLv1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=be282f1c3cc9a98cc0dc5c2b25dfc510 \
                    file://src/gstmpegdemux.h;beginline=1;endline=19;md5=a9e90033f59897b91664d9f2a2ff01dd"
LICENSE_FLAGS = "commercial"

acpaths = "-I ${S}/common/m4 -I ${S}/m4"

SRC_URI[md5sum] = "7c4fb993f80b9ae631b11897733f0970"
SRC_URI[sha256sum] = "df04c91cc8e5d9a892c2492ed989974b4547beaa2a3647649e85113317897424"
