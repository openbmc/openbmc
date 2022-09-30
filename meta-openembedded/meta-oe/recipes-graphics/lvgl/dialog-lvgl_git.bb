# SPDX-FileCopyrightText: Huawei Inc.
# SPDX-License-Identifier: MIT

SRC_URI = "gitsm://git.ostc-eu.org/rzr/dialog-lvgl;destsuffix=${S};protocol=https;nobranch=1"
SRCREV = "5d2121457a6988c97cacb0790594440693fc3d29"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8ce0a84e5276f01364119c873b712c4f"
AUTHOR = "Philippe Coval <philippe.coval.ext@huawei.com>"

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
