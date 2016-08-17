require xorg-proto-common.inc

SUMMARY = "XFree86-DRI: XFree86 Direct Rendering Infrastructure extension headers"

DESCRIPTION = "This package provides the wire protocol for the XFree86 \
Direct Rendering Infrastructure extension.  The XFree86-DRI extension is \
used to organize direct rendering support or 3D clients and help \
arbitrate requests."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ef103b9d951e39ff7e23d386e2011fa3 \
                    file://xf86driproto.h;endline=35;md5=42be3d8e6d429ab79172572bb0cff544"

PE = "1"

SRC_URI[md5sum] = "1d716d0dac3b664e5ee20c69d34bc10e"
SRC_URI[sha256sum] = "9c4b8d7221cb6dc4309269ccc008a22753698ae9245a398a59df35f1404d661f"
