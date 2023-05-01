# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: MIT

HOMEPAGE = "https://lvgl.io/"
DESCRIPTION = "LVGL is an OSS graphics library to create embedded GUI"
SUMMARY = "Light and Versatile Graphics Library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=bf1198c89ae87f043108cea62460b03a"

SRC_URI = "gitsm://github.com/lvgl/lvgl;protocol=https;nobranch=1"
SRCREV = "d38eb1e689fa5a64c25e677275172d9c8a4ab2f0"

REQUIRED_DISTRO_FEATURES = "wayland"

inherit cmake
inherit features_check

EXTRA_OECMAKE = "-DLIB_INSTALL_DIR=${baselib}"
S = "${WORKDIR}/git"

LVGL_CONFIG_LV_MEM_CUSTOM ?= "0"

# Upstream does not support a default configuration
# but propose a default "disabled" template, which is used as reference
# More configuration can be done using external configuration variables
do_configure:prepend() {
    [ -r "${S}/lv_conf.h" ] \
        || sed -e 's|#if 0 .*Set it to "1" to enable .*|#if 1 // Enabled|g' \
	    -e "s|\(#define LV_MEM_CUSTOM .*\)0|\1${LVGL_CONFIG_LV_MEM_CUSTOM}|g" \
            < "${S}/lv_conf_template.h" > "${S}/lv_conf.h"
}

FILES:${PN}-dev += "\
    ${includedir}/${PN}/ \
    ${includedir}/${PN}/lvgl/ \
    "
