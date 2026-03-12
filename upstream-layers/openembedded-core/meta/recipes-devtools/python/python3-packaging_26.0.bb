SUMMARY = "Core utilities for Python packages"
HOMEPAGE = "https://github.com/pypa/packaging"
LICENSE = "Apache-2.0 | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faadaedca9251a90b205c9167578ce91"

SRC_URI[sha256sum] = "00243ae351a257117b6a241061796684b084ed1c516a08c48a3f7e147a9d80b4"

SRC_URI += "file://run-ptest.in"

inherit pypi python_flit_core ptest

# Bootstrap the native build
DEPENDS:remove:class-native = "python3-build-native"
RDEPENDS:${PN} += "python3-profile"

# This test needs tomli_w which isn't currently in meta/
SKIPLIST = "--ignore=tests/test_pylock.py"
# Tests don't handle manylinux+musl (https://github.com/pypa/packaging/issues/850)
SKIPLIST:append:libc-musl = " --deselect=tests/test_manylinux.py::test_is_manylinux_compatible_old --deselect=tests/test_tags.py::TestManylinuxPlatform --deselect=tests/test_tags.py::TestSysTags"

do_compile:class-native () {
    python_flit_core_do_manual_build
}

do_install_ptest() {
    cp -r ${S}/tests ${D}${PTEST_PATH}/
    # We don't need this script which is used to build the binaries
    rm -f ${D}${PTEST_PATH}/tests/manylinux/build.sh
    sed -e 's|IGNOREDTESTS|${SKIPLIST}|' ${UNPACKDIR}/run-ptest.in > ${D}${PTEST_PATH}/run-ptest
    chmod 0755 ${D}${PTEST_PATH}/run-ptest
}

RDEPENDS:${PN}-ptest = "\
    python3-ctypes \
    python3-pretend \
    python3-pytest \
    python3-unittest-automake-output \
"

# The ptest package contains prebuilt test binaries
INSANE_SKIP:${PN} = "already-stripped"
INSANE_SKIP:${PN}-ptest = "arch"

BBCLASSEXTEND = "native nativesdk"
