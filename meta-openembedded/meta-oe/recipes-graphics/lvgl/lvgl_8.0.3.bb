# -*- mode: bitbake; c-basic-offset: 4; indent-tabs-mode: nil -*-

# SPDX-FileCopyrightText: Huawei Inc.
# SPDX-License-Identifier: MIT

# TODO: Pin upstream release (current is v8.0.3-dev-239-g7b7bed37d)
src_org = "lvgl"
SRC_URI = "gitsm://github.com/${src_org}/lvgl;destsuffix=${S};protocol=https;nobranch=1"
SRCREV = "7b7bed37d3e937c59ec99fccba58774fbf9f1930"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=bf1198c89ae87f043108cea62460b03a"

HOMEPAGE = "https://lvgl.io/"
SUMMARY = "Light and Versatile Graphics Library"
DESCRIPTION = "LVGL is an open-source graphics library providing everything you need to create embedded GUI with easy-to-use graphical elements, beautiful visual effects and low memory footprint."

REQUIRED_DISTRO_FEATURES = "wayland"

inherit cmake
inherit features_check

S = "${WORKDIR}/${PN}-${PV}"

EXTRA_OECMAKE += "-Dinstall:BOOL=ON"


do_configure:prepend() {
    [ -r "${S}/lv_conf.h" ] \
        || sed -e "s|#if 0 /*Set it to \"1\" to enable the content*/|#if 1 // Enabled by ${PN}|g" \
            < "${S}/lv_conf_template.h" > "${S}/lv_conf.h"
}


FILES:${PN}-dev = "\
    ${includedir}/${PN}/ \
    ${includedir}/${PN}/lvgl/ \
    "

FILES:${PN}-staticdev = "${libdir}/"
