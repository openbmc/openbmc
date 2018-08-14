require xorg-driver-video.inc

SUMMARY = "X.Org X server -- cirrus display driver"
DESCRIPTION = "cirrus is an Xorg driver for Cirrus Logic VGA adapters. These \
devices are not so common in the wild anymore, but QEMU can emulate one, so \
the driver is still useful."

LIC_FILES_CHKSUM = "file://COPYING;md5=6ddc7ca860dc5fd014e7f160ea699295"

SRC_URI[md5sum] = "7d7dfd4cdc42aa8b6e923510fa00ad2b"
SRC_URI[sha256sum] = "edc87b20a55259126b5239b5c1ef913419eab7ded0ed12ae9ae989460d7351ab"

DEPENDS += "libpciaccess"
