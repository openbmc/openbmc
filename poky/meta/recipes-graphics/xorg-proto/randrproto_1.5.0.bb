require xorg-proto-common.inc

SUMMARY = "XRandR: X Resize, Rotate and Reflect extension headers"

DESCRIPTION = "This package provides the wire protocol for the X Resize, \
Rotate and Reflect extension.  This extension provides the ability to \
resize, rotate and reflect the root window of a screen."

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=00426d41bd3d9267cf9bbb2df9323a5e \
                    file://randrproto.h;endline=30;md5=3885957c6048fdf3310ac8ba54ca2c3f"

RCONFLICTS_${PN} = "randrext"
PE = "1"

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "a46765c8dcacb7114c821baf0df1e797"
SRC_URI[sha256sum] = "4c675533e79cd730997d232c8894b6692174dce58d3e207021b8f860be498468"
