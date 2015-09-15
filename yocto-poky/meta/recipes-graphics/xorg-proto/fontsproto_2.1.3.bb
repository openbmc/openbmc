require xorg-proto-common.inc

SUMMARY = "XFont: X Font rasterisation headers"

DESCRIPTION = "This package provides the wire protocol for the X Font \
rasterisation extensions.  These extensions are used to control \
server-side font configurations."

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=c3e48aa9ce868c8e90f0401db41c11a2 \
                    file://FSproto.h;endline=44;md5=d2e58e27095e5ea7d4ad456ccb91986c"

PE = "1"

BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "36934d00b00555eaacde9f091f392f97"
SRC_URI[sha256sum] = "259046b0dd9130825c4a4c479ba3591d6d0f17a33f54e294b56478729a6e5ab8"
