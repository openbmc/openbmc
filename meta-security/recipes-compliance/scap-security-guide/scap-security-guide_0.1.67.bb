# Copyright (C) 2017 - 2023 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "SCAP content for various platforms, upstream version"
HOME_URL = "https://www.open-scap.org/security-policies/scap-security-guide/"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9bfa86579213cb4c6adaffface6b2820"
LICENSE = "BSD-3-Clause"

SRCREV = "dad85502ce8da722a6afc391346c41cee61e90a9"
SRC_URI = "git://github.com/ComplianceAsCode/content.git;branch=master;protocol=https \
           file://0001-scap-security-guide-add-openembedded.patch \
           file://0001-standard.profile-expand-checks.patch \
           file://0001-scap-security-guide-add-Poky-support.patch \
           file://run_eval.sh \
           "


DEPENDS = "openscap-native python3-pyyaml-native python3-jinja2-native libxml2-native expat-native coreutils-native"

S = "${WORKDIR}/git"
B = "${S}/build"

inherit cmake pkgconfig python3native python3targetconfig

OECMAKE_GENERATOR = "Unix Makefiles"

EXTRA_OECMAKE += "-DENABLE_PYTHON_COVERAGE=OFF -DSSG_PRODUCT_DEFAULT=OFF -DSSG_PRODUCT_OE=ON"

do_configure[depends] += "openscap-native:do_install"

do_configure:prepend () {
    sed -i -e 's:NAMES\ sed:NAMES\ ${HOSTTOOLS_DIR}/sed:g' ${S}/CMakeLists.txt
    sed -i -e 's:NAMES\ grep:NAMES\ ${HOSTTOOLS_DIR}/grep:g' ${S}/CMakeLists.txt
}

do_install:append() {
    install -d ${D}${datadir}/openscap
    install  ${WORKDIR}/run_eval.sh ${D}${datadir}/openscap/.
}

FILES:${PN} += "${datadir}/xml ${datadir}/openscap"

RDEPENDS:${PN} = "openscap"

COMPATIBLE_HOST:libc-musl = "null"
