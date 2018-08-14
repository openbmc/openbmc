require xorg-proto-common.inc

SUMMARY = "XRecord: X Record extension headers"

DESCRIPTION = "This package provides the wire protocol for the X Record \
extension.  This extension is used to record and play back event \
sequences."

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=575827a0f554bbed332542976d5f3d40 \
                    file://recordproto.h;endline=19;md5=1cbb0dd45a0b060ff833901620a3e738"

RCONFLICTS_${PN} = "recordext"
PR = "r1"
PE = "1"

SRC_URI[md5sum] = "1b4e5dede5ea51906f1530ca1e21d216"
SRC_URI[sha256sum] = "a777548d2e92aa259f1528de3c4a36d15e07a4650d0976573a8e2ff5437e7370"
