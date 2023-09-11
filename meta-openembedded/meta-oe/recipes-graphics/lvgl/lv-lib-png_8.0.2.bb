# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: MIT

HOMEPAGE = "https://docs.lvgl.io"
SUMMARY = "PNG decoder for LVGL"
DESCRIPTION = "Allow the use of PNG images in LVGL. This implementation uses lodepng"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6fc0df890c5270ef045981b516bb8f2"

SRC_URI = "git://github.com/lvgl/lv_lib_png;;protocol=https;branch=master"
SRCREV = "bf1531afe07c9f861107559e29ab8a2d83e4715a"

S = "${WORKDIR}/git"

# because of lvgl dependency
REQUIRED_DISTRO_FEATURES = "wayland"

DEPENDS += "lvgl"

EXTRA_OECMAKE += "-DLIB_INSTALL_DIR=${baselib}"

inherit cmake
inherit features_check

TARGET_CFLAGS += "-DLV_CONF_INCLUDE_SIMPLE=1"
TARGET_CFLAGS += "-I${STAGING_INCDIR}/lvgl"

FILES:${PN}-dev = "\
    ${includedir}/lvgl/lv_lib_png/ \
    "
