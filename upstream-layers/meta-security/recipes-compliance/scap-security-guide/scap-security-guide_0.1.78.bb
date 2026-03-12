# Copyright (C) 2017 - 2024 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "SCAP content for various platforms, upstream version"
HOME_URL = "https://www.open-scap.org/security-policies/scap-security-guide/"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9bfa86579213cb4c6adaffface6b2820"
LICENSE = "BSD-3-Clause"

SRCREV = "f7d794851971087db77d4be8eeb716944a1aae21"
SRC_URI = "git://github.com/ComplianceAsCode/content.git;protocol=https;branch=stable \
           file://run_eval.sh \
           "

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)$"

DEPENDS = "openscap-native python3-pyyaml-native python3-jinja2-native libxml2-native expat-native coreutils-native"

B = "${S}/build"

inherit cmake pkgconfig python3native python3targetconfig

STAGING_OSCAP_BUILDDIR = "${TMPDIR}/work-shared/openscap/oscap-build-artifacts"
export OSCAP_CPE_PATH = "${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/cpe"
export OSCAP_SCHEMA_PATH = "${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/schemas"
export OSCAP_XSLT_PATH = "${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/xsl"

OECMAKE_GENERATOR = "Unix Makefiles"

EXTRA_OECMAKE += "-DENABLE_PYTHON_COVERAGE=OFF -DSSG_PRODUCT_DEFAULT=OFF -DSSG_PRODUCT_OPENEMBEDDED=ON"

do_configure[depends] += "openscap-native:do_install"

do_configure:prepend () {
    sed -i -e 's:NAMES\ sed:NAMES\ ${HOSTTOOLS_DIR}/sed:g' ${S}/CMakeLists.txt
    sed -i -e 's:NAMES\ grep:NAMES\ ${HOSTTOOLS_DIR}/grep:g' ${S}/CMakeLists.txt
}

do_install:append() {
    install -d ${D}${datadir}/openscap
    install  ${UNPACKDIR}/run_eval.sh ${D}${datadir}/openscap/.
}

FILES:${PN} += "${datadir}/xml ${datadir}/openscap"

RDEPENDS:${PN} = "openscap"

COMPATIBLE_HOST:libc-musl = "null"
