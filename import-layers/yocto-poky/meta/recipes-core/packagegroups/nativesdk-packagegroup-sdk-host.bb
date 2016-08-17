#
# Copyright (C) 2007 OpenedHand Ltd
#

SUMMARY = "Host packages for the standalone SDK or external toolchain"
PR = "r12"
LICENSE = "MIT"

inherit packagegroup nativesdk

PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

RDEPENDS_${PN} = "\
    nativesdk-pkgconfig \
    nativesdk-qemu \
    nativesdk-qemu-helper \
    nativesdk-pseudo \
    nativesdk-unfs3 \
    nativesdk-opkg \
    nativesdk-libtool \
    nativesdk-autoconf \
    nativesdk-automake \
    nativesdk-shadow \
    nativesdk-makedevs \
    nativesdk-smartpm \
    nativesdk-postinst-intercept \
    "

RDEPENDS_${PN}_darwin = "\
    nativesdk-pkgconfig \
    nativesdk-opkg \
    nativesdk-libtool \
    "
