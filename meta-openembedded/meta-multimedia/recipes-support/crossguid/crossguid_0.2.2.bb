# Copyright (C) 2017 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Lightweight cross platform C++ GUID/UUID library"
HOMEPAGE = "https://github.com/graeme-hill/crossguid"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1373274bc8d8001edc54933919f36f68"
DEPENDS += "util-linux"

SRCREV = "ca1bf4b810e2d188d04cb6286f957008ee1b7681"
SRC_URI = "git://github.com/graeme-hill/crossguid;protocol=https;branch=master \
           file://0001-include-missing-cstdint.patch"

S = "${WORKDIR}/git"

inherit cmake

do_install:append() {
    sed -i -e 's|${STAGING_DIR_HOST}||g' ${D}${datadir}/crossguid/cmake/crossguid-config.cmake
}
