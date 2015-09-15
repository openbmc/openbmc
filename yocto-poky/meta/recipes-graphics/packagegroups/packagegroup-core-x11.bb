#
# Copyright (C) 2011 Intel Corporation
#

LICENSE = "MIT"
PR = "r40"

inherit packagegroup distro_features_check
# rdepends on x11-common
REQUIRED_DISTRO_FEATURES = "x11"

PACKAGES = "${PN} ${PN}-utils"

# xserver-common, x11-common
VIRTUAL-RUNTIME_xserver_common ?= "x11-common"

# elsa, xserver-nodm-init
VIRTUAL-RUNTIME_graphical_init_manager ?= "xserver-nodm-init"

SUMMARY = "X11 display server and basic utilities"
RDEPENDS_${PN} = "\
    ${PN}-xserver \
    ${PN}-utils \
    "

SUMMARY_${PN}-utils = "X11 basic utilities and init"
RDEPENDS_${PN}-utils = "\
    ${VIRTUAL-RUNTIME_xserver_common} \
    ${VIRTUAL-RUNTIME_graphical_init_manager} \
    xauth \
    xhost \
    xset \
    xrandr \
    "
