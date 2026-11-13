require xorg-lib-common.inc

SUMMARY = "XTest: X Test extension library"

DESCRIPTION = "This extension is a minimal set of client and server \
extensions required to completely test the X11 server with no user \
intervention."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=bb4f89972c3869f617f61c1a79ad1952 \
                    file://src/XTest.c;beginline=2;endline=32;md5=b1c8c9dff842b4d5b89ca5fa32c40e99"

DEPENDS += "libxext xorgproto libxi"
PROVIDES = "xtst"
PE = "1"

XORG_PN = "libXtst"
SRC_URI[sha256sum] = "f94f0b37c07d80d877f3eda2c2405cc7e62aa9842f6310354aa349e3d66a4fe5"

BBCLASSEXTEND = "native nativesdk"
