# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: MIT

SUMMARY = "Light and Versatile Graphics Library"
DESCRIPTION = "LVGL is an OSS graphics library to create embedded GUIs."
HOMEPAGE = "https://lvgl.io/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=4570b6241b4fced1d1d18eb691a0e083"

SRC_URI = "git://github.com/lvgl/lvgl;protocol=https;branch=release/v9.5;tag=v9.5.0"

SRCREV = "85aa60d18b3d5e5588d7b247abf90198f07c8a63"

inherit cmake

EXTRA_OECMAKE += "-DLIB_INSTALL_DIR=${baselib} -DBUILD_SHARED_LIBS=ON"

require lv-conf.inc
