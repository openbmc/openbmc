# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: MIT

SUMMARY = "Light and Versatile Graphics Library"
DESCRIPTION = "LVGL is an OSS graphics library to create embedded GUIs."
HOMEPAGE = "https://lvgl.io/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=4570b6241b4fced1d1d18eb691a0e083"

SRC_URI = "git://github.com/lvgl/lvgl;protocol=https;branch=release/v9.6;tag=v9.6.0 \
           file://0001-build-cmake-generate-proper-.so-links-when-installin.patch \
           "

SRCREV = "80ca777e37a2b176770726a02e07a6fb79ef0b39"

inherit cmake pkgconfig

EXTRA_OECMAKE += "-DLIB_INSTALL_DIR=${baselib} -DBUILD_SHARED_LIBS=ON"

require lv-conf.inc
