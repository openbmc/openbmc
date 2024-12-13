SUMMARY = "Core utilities for Python packages"
HOMEPAGE = "https://github.com/pypa/packaging"
LICENSE = "Apache-2.0 | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=faadaedca9251a90b205c9167578ce91"

SRC_URI[sha256sum] = "026ed72c8ed3fcce5bf8950572258698927fd1dbda10a5e981cdf0ac37f4f002"

SRC_URI += "file://run-ptest"

inherit pypi python_flit_core ptest

BBCLASSEXTEND = "native nativesdk"

# Bootstrap the native build
DEPENDS:remove:class-native = "python3-build-native"
RDEPENDS:${PN} += "python3-profile"

do_compile:class-native () {
    python_flit_core_do_manual_build
}

do_install_ptest() {
    cp -r ${S}/tests ${D}${PTEST_PATH}/
    # We don't need this script which is used to build the binaries
    rm -f ${D}${PTEST_PATH}/tests/manylinux/build.sh
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
