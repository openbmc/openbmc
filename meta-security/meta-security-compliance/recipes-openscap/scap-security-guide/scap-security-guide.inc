# Copyright (C) 2017 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "SCAP content for various platforms"
HOME_URL = "https://www.open-scap.org/security-policies/scap-security-guide/"
LIC_FILES_CHKSUM = "file://LICENSE;md5=97662e4486d9a1d09f358851d9f41a1a"
LICENSE = "LGPL-2.1"

DEPENDS = "openscap-native python3 python3-pyyaml-native python3-jinja2-native libxml2-native"

S = "${WORKDIR}/git"

inherit cmake pkgconfig python3native

STAGING_OSCAP_BUILDDIR = "${TMPDIR}/work-shared/openscap/oscap-build-artifacts"
export OSCAP_CPE_PATH="${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/cpe"
export OSCAP_SCHEMA_PATH="${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/schemas"
export OSCAP_XSLT_PATH="${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/xsl"

OECMAKE_GENERATOR = "Unix Makefiles"

EXTRA_OECMAKE += "-DENABLE_PYTHON_COVERAGE=OFF"

B = "${S}/build"

do_configure[depends] += "openscap-native:do_install"

do_configure_prepend () {
    sed -i -e 's:NAMES\ sed:NAMES\ ${HOSTTOOLS_DIR}/sed:g' ${S}/CMakeLists.txt
    sed -i -e 's:NAMES\ grep:NAMES\ ${HOSTTOOLS_DIR}/grep:g' ${S}/CMakeLists.txt
}

FILES_${PN} += "${datadir}/xml"

RDEPENDS_${PN} = "openscap"
