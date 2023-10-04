#
# Copyright (C) 2011 Intel Corporation
#


inherit packagegroup features_check
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGES = "${PN} ${PN}-utils"

# backwards compatibility for xserver-common
VIRTUAL-RUNTIME_xserver_common ?= ""

# elsa, xserver-nodm-init
VIRTUAL-RUNTIME_graphical_init_manager ?= "xserver-nodm-init"

SUMMARY = "X11 display server and basic utilities"
RDEPENDS:${PN} = "\
    ${PN}-xserver \
    ${PN}-utils \
    "

SUMMARY:${PN}-utils = "X11 basic utilities and init"
RDEPENDS:${PN}-utils = "\
    ${VIRTUAL-RUNTIME_xserver_common} \
    ${VIRTUAL-RUNTIME_graphical_init_manager} \
    xauth \
    xhost \
    xset \
    xrandr \
    xmodmap \
    xdpyinfo \
    xinput-calibrator \
    dbus-x11 \
    "
