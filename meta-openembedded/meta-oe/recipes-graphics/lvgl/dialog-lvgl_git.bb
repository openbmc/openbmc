# SPDX-FileCopyrightText: Huawei Inc.
# SPDX-License-Identifier: MIT

SRC_URI = "git://git.ostc-eu.org/rzr/dialog-lvgl;destsuffix=${S};protocol=https;nobranch=1 \
           file://0001-wayland-Switch-to-custom-timer-tick.patch \
           file://0002-wayland-Fix-callback-data-type.patch \
           "
SRCREV = "cdf8d38acca87e871c3a488fd07f1e4779590f8e"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8ce0a84e5276f01364119c873b712c4f"

DEPENDS += "lvgl"
DEPENDS += "lv-drivers"

SUMMARY = "Basic UI utility to be used in scripts"
DESCRIPTION = "Inspired by ncurses' dialog, implemented using LVGL"
HOMEPAGE = "https://git.ostc-eu.org/rzr/dialog-lvgl/-/wikis/"

REQUIRED_DISTRO_FEATURES = "wayland"

inherit pkgconfig
inherit features_check

EXTRA_OEMAKE += "sysroot=${RECIPE_SYSROOT}"
EXTRA_OEMAKE += "DESTDIR=${D}"
EXTRA_OEMAKE += "lvgl_driver=wayland"

do_install() {
    oe_runmake install
}
