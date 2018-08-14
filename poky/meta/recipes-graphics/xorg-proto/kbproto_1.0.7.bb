require xorg-proto-common.inc

SUMMARY = "XKB: X Keyboard extension headers"

DESCRIPTION = "This package provides the wire protocol for the X \
Keyboard extension.  This extension is used to control options related \
to keyboard handling and layout."

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=7dd6ea99e2a83a552c02c80963623c38 \
                    file://XKBproto.h;beginline=1;endline=25;md5=5744eeff407aeb6e7a1346eebab486a2"

PE = "1"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "94afc90c1f7bef4a27fdd59ece39c878"
SRC_URI[sha256sum] = "f882210b76376e3fa006b11dbd890e56ec0942bc56e65d1249ff4af86f90b857"
