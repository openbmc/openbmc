# SPDX-FileCopyrightText: Huawei Inc.
# SPDX-License-Identifier: MIT

# TODO: Pin upstream release (current v7.11.0-80-g419a757)
src_org = "lvgl"
SRC_URI = "gitsm://github.com/${src_org}/lv_drivers;destsuffix=${S};protocol=https;nobranch=1"
SRCREV = "419a757c23aaa67c676fe3a2196d64808fcf2254"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6fc0df890c5270ef045981b516bb8f2"

HOMEPAGE = "https://docs.lvgl.io/latest/en/html/porting/index.html"
SUMMARY = "LVGL's Display and Touch pad drivers"
DESCRIPTION = "Collection of drivers: SDL, framebuffer, wayland and more..."

DEPENDS += "libxkbcommon"
DEPENDS += "lvgl"
DEPENDS += "wayland"

REQUIRED_DISTRO_FEATURES = "wayland"

inherit cmake
inherit features_check

S = "${WORKDIR}/${PN}-${PV}"

EXTRA_OECMAKE += "-Dinstall:BOOL=ON"

TARGET_CFLAGS += "-DLV_CONF_INCLUDE_SIMPLE=1"
TARGET_CFLAGS += "-I${RECIPE_SYSROOT}/${includedir}/lvgl"


do_configure:append() {
    [ -r "${S}/lv_drv_conf.h" ] \
        || sed -e "s|#if 0 .*Set it to \"1\" to enable the content.*|#if 1 // Enabled by ${PN}|g" \
               -e "s|#  define USE_WAYLAND       0|#  define USE_WAYLAND       1|g" \
          < "${S}/lv_drv_conf_template.h" > "${S}/lv_drv_conf.h"
}


FILES:${PN}-dev = "\
    ${includedir}/lvgl/lv_drivers/ \
    "

FILES:${PN}-staticdev = "${libdir}/"
