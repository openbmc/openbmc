SUMMARY = "Core utilities for Python packages"
HOMEPAGE = "https://github.com/pypa/packaging"
LICENSE = "Apache-2.0 | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faadaedca9251a90b205c9167578ce91"

SRC_URI[sha256sum] = "c228a6dc5e932d346bc5739379109d49e8853dd8223571c7c5b55260edc0b97f"

SRC_URI += "file://run-ptest.in"

inherit pypi python_flit_core ptest

BBCLASSEXTEND = "native nativesdk"

# Bootstrap the native build
DEPENDS:remove:class-native = "python3-build-native"
RDEPENDS:${PN} += "python3-profile"

# https://github.com/pypa/packaging/issues/850
SKIPLIST ?= ""
SKIPLIST:libc-musl = "--deselect tests/test_manylinux.py::test_is_manylinux_compatible_old --ignore=tests/test_tags.py"

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
