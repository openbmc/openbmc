#
# Copyright (C) 2011 Intel Corporation
#

SUMMARY = "X11 display server"
PR = "r40"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup features_check
# rdepends on XSERVER
REQUIRED_DISTRO_FEATURES = "x11"

XSERVER ?= "xserver-xorg xf86-video-fbdev"
XSERVERCODECS ?= ""

RDEPENDS_${PN} = "\
    ${XSERVER} \
    ${XSERVERCODECS} \
    "
