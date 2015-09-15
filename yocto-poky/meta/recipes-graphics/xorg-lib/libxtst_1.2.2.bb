require xorg-lib-common.inc

SUMMARY = "XTest: X Test extension library"

DESCRIPTION = "This extension is a minimal set of client and server \
extensions required to completely test the X11 server with no user \
intervention."

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=bb4f89972c3869f617f61c1a79ad1952 \
                    file://src/XTest.c;beginline=2;endline=32;md5=b1c8c9dff842b4d5b89ca5fa32c40e99"

DEPENDS += "libxext recordproto inputproto libxi"
PROVIDES = "xtst"
PE = "1"

XORG_PN = "libXtst"

SRC_URI[md5sum] = "25c6b366ac3dc7a12c5d79816ce96a59"
SRC_URI[sha256sum] = "ef0a7ffd577e5f1a25b1663b375679529663a1880151beaa73e9186c8309f6d9"
