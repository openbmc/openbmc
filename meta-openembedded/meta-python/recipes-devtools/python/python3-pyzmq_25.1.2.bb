SUMMARY = "PyZMQ: Python bindings for ZMQ"
DESCRIPTION = "This package contains Python bindings for ZeroMQ. ZMQ is a lightweight and fast messaging implementation."
HOMEPAGE = "http://zeromq.org/bindings:python"
LICENSE = "BSD-3-Clause & LGPL-3.0-only"
LIC_FILES_CHKSUM = "\
    file://LICENSE.BSD;md5=1787206f198344195a671b60326c59dc \
    file://LICENSE.LESSER;md5=0e99bfbdd8b9d33b0221986fe3be89ed \
"

DEPENDS = "python3-packaging-native python3-cython-native python3-setuptools-scm-native zeromq"

SRC_URI:append = " \
    file://club-rpath-out.patch \
    file://run-ptest \
"
SRC_URI[sha256sum] = "93f1aa311e8bb912e34f004cf186407a4e90eec4f0ecc0efd26056bf7eda0226"

inherit pypi pkgconfig python_setuptools_build_meta ptest

PACKAGES =+ "\
    ${PN}-test \
"

FILES:${PN}-test += "\
    ${libdir}/${PYTHON_DIR}/site-packages/*/tests \
"

RDEPENDS:${PN} += "\
        python3-json \
        python3-multiprocessing \
        python3-tornado \
"

RDEPENDS:${PN}-ptest += "\
    ${PN}-test \
    python3-pytest \
    python3-unittest-automake-output \
    python3-unixadmin \
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
