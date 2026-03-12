SUMMARY = "PyZMQ: Python bindings for ZMQ"
DESCRIPTION = "This package contains Python bindings for ZeroMQ. ZMQ is a lightweight and fast messaging implementation."
HOMEPAGE = "https://zeromq.org/bindings:python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "\
    file://LICENSE.md;md5=1787206f198344195a671b60326c59dc \
"

DEPENDS = "python3-packaging-native \
           python3-setuptools-scm-native \
           python3-scikit-build-core-native \
           cmake-native \
           ninja-native \
           zeromq \
"

PEP517_BUILD_OPTS = "--skip-dependency-check \
                     -Ccmake.define.PYZMQ_LIBZMQ_RPATH=OFF \
                     -Ccmake.define.PYZMQ_NO_BUNDLE=ON \
                     -Ccmake.define.CMAKE_FIND_ROOT_PATH_MODE_PACKAGE=ONLY \
                     -Ccmake.define.CMAKE_FIND_ROOT_PATH_MODE_PROGRAM=ONLY \
                     -Ccmake.define.CMAKE_FIND_ROOT_PATH_MODE_LIBRARY=ONLY \
                     -Ccmake.define.CMAKE_FIND_ROOT_PATH_MODE_INCLUDE=ONLY \
                     -Ccmake.define.CMAKE_FIND_ROOT_PATH="${STAGING_DIR_NATIVE}" \
                     -Ccmake.build-type="RelWithDebInfo" \
                     -Cbuild-dir="${B}" \
"

SRC_URI:append = " \
    file://run-ptest \
"
SRC_URI[sha256sum] = "ac0765e3d44455adb6ddbf4417dcce460fc40a05978c08efdf2948072f6db540"

inherit pypi pkgconfig python_setuptools_build_meta ptest cython

PACKAGES =+ "\
    ${PN}-test \
"

FILES:${PN}-test += "\
    ${PYTHON_SITEPACKAGES_DIR}/*/tests \
"

RDEPENDS:${PN} += "\
        python3-json \
        python3-multiprocessing \
        python3-tornado \
"

RDEPENDS:${PN}-ptest += "\
    ${PN}-test \
    python3-pytest \
    python3-asyncio \
    python3-pytest-asyncio \
    python3-unittest-automake-output \
    python3-unixadmin \
"

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
        install -m 0644 ${S}/pytest.ini ${D}${PTEST_PATH}/pytest.ini
}
