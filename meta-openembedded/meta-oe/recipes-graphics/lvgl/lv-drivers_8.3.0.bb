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
PACKAGECONFIG[fbdev] = ",,"
PACKAGECONFIG[wayland] = ",,libxkbcommon wayland"
LVGL_CONFIG_USE_FBDEV = "${@bb.utils.contains('PACKAGECONFIG', 'fbdev', '1', '0', d)}"
LVGL_CONFIG_USE_WAYLAND = "${@bb.utils.contains('PACKAGECONFIG', 'wayland', '1', '0', d)}"

inherit cmake

S = "${WORKDIR}/git"

LVGL_CONFIG_WAYLAND_HOR_RES ?= "480"
LVGL_CONFIG_WAYLAND_VER_RES ?= "320"

EXTRA_OECMAKE += "-Dinstall:BOOL=ON -DLIB_INSTALL_DIR=${baselib}"

TARGET_CFLAGS += "-DLV_CONF_INCLUDE_SIMPLE=1"
TARGET_CFLAGS += "-I${STAGING_INCDIR}/lvgl"

# Upstream does not support a default configuration
# but propose a default "disabled" template, which is used as reference
# More configuration can be done using external configuration variables
do_configure:append() {
    [ -r "${S}/lv_drv_conf.h" ] \
        || sed -e "s|#if 0 .*Set it to \"1\" to enable the content.*|#if 1 // Enabled by ${PN}|g" \
	       \
               -e "s|\(^#  define USE_FBDEV \).*|#  define USE_FBDEV ${LVGL_CONFIG_USE_FBDEV}|g" \
               -e "s|\(^#  define USE_EVDEV \).*|#  define USE_EVDEV ${LVGL_CONFIG_USE_FBDEV}|g" \
	       \
               -e "s|\(^#  define USE_WAYLAND \).*|#  define USE_WAYLAND ${LVGL_CONFIG_USE_WAYLAND}|g" \
	       -e "s|\(^ *# *define *WAYLAND_HOR_RES *\).*|\1${LVGL_CONFIG_WAYLAND_HOR_RES}|g" \
 	       -e "s|\(^ *# *define *WAYLAND_VER_RES *\).*|\1${LVGL_CONFIG_WAYLAND_VER_RES}|g" \
          < "${S}/lv_drv_conf_template.h" > "${S}/lv_drv_conf.h"
}

FILES:${PN}-dev += "\
    ${includedir}/lvgl/lv_drivers/ \
    "
