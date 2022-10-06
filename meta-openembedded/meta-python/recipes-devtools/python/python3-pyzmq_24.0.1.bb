SUMMARY = "PyZMQ: Python bindings for ZMQ"
DESCRIPTION = "This package contains Python bindings for ZeroMQ. ZMQ is a lightweight and fast messaging implementation."
HOMEPAGE = "http://zeromq.org/bindings:python"
LICENSE = "BSD-3-Clause & LGPL-3.0-only"
LIC_FILES_CHKSUM = "\
    file://COPYING.BSD;md5=11c65680f637c3df7f58bbc8d133e96e \
    file://COPYING.LESSER;md5=12c592fa0bcfff3fb0977b066e9cb69e \
"

DEPENDS = "python3-packaging-native zeromq"

SRC_URI:append = " \
    file://club-rpath-out.patch \
    file://run-ptest \
"
SRC_URI[sha256sum] = "216f5d7dbb67166759e59b0479bca82b8acf9bed6015b526b8eb10143fb08e77"

inherit pypi pkgconfig python_setuptools_build_meta ptest

PACKAGES =+ "\
    ${PN}-test \
"

FILES:${PN}-test += "\
    ${libdir}/${PYTHON_DIR}/site-packages/*/tests \
"

RDEPENDS:${PN} += "\
        ${PYTHON_PN}-json \
        ${PYTHON_PN}-multiprocessing \
"

RDEPENDS:${PN}-ptest += "\
        ${PN}-test \
        ${PYTHON_PN}-pytest \
"

do_compile:prepend() {
    echo [global] > ${S}/setup.cfg
    echo zmq_prefix = ${STAGING_DIR_HOST} >> ${S}/setup.cfg
    echo have_sys_un_h = True >> ${S}/setup.cfg
    echo skip_check_zmq = True >> ${S}/setup.cfg
    echo libzmq_extension = False >> ${S}/setup.cfg
    echo no_libzmq_extension = True >> ${S}/setup.cfg
}

do_install:append() {
        sed -i -e 's#${RECIPE_SYSROOT}##g' ${D}${PYTHON_SITEPACKAGES_DIR}/zmq/utils/config.json
        sed -i -e 's#${RECIPE_SYSROOT}##g' ${D}${PYTHON_SITEPACKAGES_DIR}/zmq/utils/compiler.json
}

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/zmq/tests/* ${D}${PTEST_PATH}/tests/
}
