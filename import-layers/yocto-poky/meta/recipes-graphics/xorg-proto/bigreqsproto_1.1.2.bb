require xorg-proto-common.inc

PE = "1"

SUMMARY = "BigReqs: X Big Requests extension headers"

DESCRIPTION = "This package provides the wire protocol for the \
BIG-REQUESTS extension, used to send larger requests that usual in order \
to avoid fragmentation."

BBCLASSEXTEND = "native nativesdk"

LIC_FILES_CHKSUM = "file://COPYING;md5=b12715630da6f268d0d3712ee1a504f4"

SRC_URI[md5sum] = "1a05fb01fa1d5198894c931cf925c025"
SRC_URI[sha256sum] = "462116ab44e41d8121bfde947321950370b285a5316612b8fce8334d50751b1e"
