# Copyright (C) 2017 - 2024 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "SCAP content for various platforms, upstream version"
HOME_URL = "https://www.open-scap.org/security-policies/scap-security-guide/"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9bfa86579213cb4c6adaffface6b2820"
LICENSE = "BSD-3-Clause"

SRCREV = "1bf21b05fa9581e8ca44e104e741e13fad3551ef"
SRC_URI = "git://github.com/ComplianceAsCode/content.git;branch=stable;protocol=https \
           file://run_eval.sh \
           file://run-ptest \
           "


DEPENDS = "openscap-native python3-pyyaml-native python3-jinja2-native libxml2-native expat-native coreutils-native"

S = "${UNPACKDIR}/git"
B = "${S}/build"

inherit cmake pkgconfig python3native python3targetconfig ptest

STAGING_OSCAP_BUILDDIR = "${TMPDIR}/work-shared/openscap/oscap-build-artifacts"
export OSCAP_CPE_PATH="${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/cpe"
export OSCAP_SCHEMA_PATH="${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/schemas"
export OSCAP_XSLT_PATH="${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/xsl"

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

do_compile_ptest() {
    cd ${S}/build
    cmake ../
    make 
}

do_install_ptest() {

    # remove host & work dir from tests
    for x in $(find ${S}/build -type f) ;
    do
       sed -e 's#${HOSTTOOLS_DIR}/##g' \
           -e 's#${RECIPE_SYSROOT_NATIVE}##g' \
           -e 's#${UNPACKDIR}#${PTEST_PATH}#g' \
           -e 's#/.*/xmllint#/usr/bin/xmllint#g' \
           -e 's#/.*/oscap#/usr/bin/oscap#g' \
           -e 's#/python3-native##g' \
           -i ${x}
    done

    for x in $(find ${S}/build-scripts -type f) ;
    do
       sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' ${x}
    done

    for x in $(find ${S}/tests -type f) ;
    do
       sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' ${x}
    done

    for x in $(find ${S}/utils -type f) ;
    do
       sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' ${x}
    done

    PDIRS="apple_os build controls products shared components applications linux_os ocp-resources tests utils ssg build-scripts"
    t=${D}/${PTEST_PATH}/git
    for d in ${PDIRS}; do
        install -d ${t}/$d
        cp -fr ${S}/$d/* ${t}/$d/.
    done

    # Remove __pycache__ directories as they contain references to TMPDIR
    for pycachedir in $(find ${D}/${PTEST_PATH} -name __pycache__); do
        rm -rf $pycachedir
    done
}

FILES:${PN} += "${datadir}/xml ${datadir}/openscap"

RDEPENDS:${PN} = "openscap"
RDEPENDS:${PN}-ptest = "cmake grep sed bash git python3 python3-modules python3-mypy python3-pyyaml python3-yamlpath python3-xmldiff python3-json2html python3-pandas python3-openpyxl python3-pytest libxml2-utils libxslt-bin"

COMPATIBLE_HOST:libc-musl = "null"
