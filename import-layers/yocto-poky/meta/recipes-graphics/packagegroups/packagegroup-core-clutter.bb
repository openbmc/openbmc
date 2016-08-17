#
# Copyright (C) 2007 OpenedHand Ltd.
#

SUMMARY = "Clutter package groups"
LICENSE = "MIT"

PR = "r6"

inherit packagegroup distro_features_check
# rdepends on clutter-*
REQUIRED_DISTRO_FEATURES = "opengl"

PACKAGES = "\
    ${PN}-core \
    "

SUMMARY_${PN}-core = "Clutter graphics library"
RDEPENDS_${PN}-core = "\
    clutter-1.0 \
    clutter-gst-3.0 \
    clutter-gtk-1.0 \
    "
