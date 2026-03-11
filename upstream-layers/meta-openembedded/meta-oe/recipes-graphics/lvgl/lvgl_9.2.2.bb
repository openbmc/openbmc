# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: MIT

HOMEPAGE = "https://lvgl.io/"
DESCRIPTION = "LVGL is an OSS graphics library to create embedded GUI"
SUMMARY = "Light and Versatile Graphics Library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.txt;md5=bf1198c89ae87f043108cea62460b03a"

SRC_URI = "\
	git://github.com/lvgl/lvgl;protocol=https;branch=release/v9.2 \
    file://0001-thorvg-fix-build-with-gcc-15.patch \
	"
SRCREV = "7f07a129e8d77f4984fff8e623fd5be18ff42e74"

inherit cmake

EXTRA_OECMAKE = "-DLIB_INSTALL_DIR=${baselib} -DBUILD_SHARED_LIBS=ON"

require lv-conf.inc

do_install:append() {
    install -m 0644 "${S}/lv_conf.h" "${D}${includedir}/${BPN}/lv_conf.h"
}
