#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "Testing tools/applications"

PR = "r2"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

# kexec-tools doesn't work on e5500-64b and nios2 yet
KEXECTOOLS ?= "kexec"
KEXECTOOLS_e5500-64b ?= ""
KEXECTOOLS_nios2 ?= ""

X11GLTOOLS = "\
    mesa-demos \
    "

3GTOOLS = "\
    ofono-tests \
    "

X11TOOLS = "\
    fstests \
    gst-player \
    x11perf \
    xrestop \
    xwininfo \
    xprop \
    xvideo-tests \
    "

RDEPENDS_${PN} = "\
    blktool \
    lrzsz \
    ${KEXECTOOLS} \
    alsa-utils-amixer \
    alsa-utils-aplay \
    ltp \
    connman-tools \
    connman-tests \
    connman-client \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', "${X11TOOLS}", "", d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', "${X11GLTOOLS}", "", d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', '3g', "${3GTOOLS}", "", d)} \
    "
