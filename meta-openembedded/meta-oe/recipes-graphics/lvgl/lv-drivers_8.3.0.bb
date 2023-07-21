# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: MIT

HOMEPAGE = "https://docs.lvgl.io/latest/en/html/porting/index.html"
SUMMARY = "LVGL's Display and Touch pad drivers"
DESCRIPTION = "Collection of drivers: SDL, framebuffer, wayland and more..."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6fc0df890c5270ef045981b516bb8f2"

SRC_URI = "git://github.com/lvgl/lv_drivers;protocol=https;branch=release/v8.3"
SRCREV = "71830257710f430b6d8d1c324f89f2eab52488f1"

DEPENDS = "lvgl"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'wayland fbdev', d)}"
require lv-drivers.inc

inherit cmake

S = "${WORKDIR}/git"

TARGET_CFLAGS += "-DLV_CONF_INCLUDE_SIMPLE=1"
TARGET_CFLAGS += "-I${STAGING_INCDIR}/lvgl"

FILES:${PN}-dev += "\
    ${includedir}/lvgl/lv_drivers/ \
    "
