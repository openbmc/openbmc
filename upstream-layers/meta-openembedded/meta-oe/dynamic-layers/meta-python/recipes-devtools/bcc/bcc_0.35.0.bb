SUMMARY = "BPF Compiler Collection (BCC)"
HOMEPAGE = "https://github.com/iovisor/bcc"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e3fc50a88d0a364313df4b21ef20c29e"

inherit cmake python3native manpages ptest

DEPENDS += "bison-native \
            flex-native \
            zip-native \
            flex \
            elfutils \
            clang \
            libbpf \
            python3-setuptools-native \
            "

RDEPENDS:${PN} += "bash python3 python3-core python3-setuptools xz"
RDEPENDS:${PN}-ptest = "kernel-devsrc packagegroup-core-buildessential cmake bash python3 python3-netaddr python3-pyroute2"

SRC_URI = "gitsm://github.com/iovisor/bcc;branch=master;protocol=https;tag=v${PV} \
           file://0001-CMakeLists.txt-override-the-PY_CMD_ESCAPED.patch \
           file://0001-Vendor-just-enough-extra-headers-to-allow-libbpf-to-.patch \
           file://0001-Fix-a-build-failure-with-clang21-5369.patch \
           file://run-ptest \
           file://ptest_wrapper.sh \
           file://fix_for_memleak.patch \
           "

SRCREV = "c31a1ca305f787ba53e001ead45ebf65233a32cf"

PACKAGECONFIG ??= "examples"
PACKAGECONFIG:remove:libc-musl = "examples"

PACKAGECONFIG[manpages] = "-DENABLE_MAN=ON,-DENABLE_MAN=OFF,"
PACKAGECONFIG[examples] = "-DENABLE_EXAMPLES=ON,-DENABLE_EXAMPLES=OFF,"

EXTRA_OECMAKE = " \
    -DREVISION='${PV}' \
    -DCMAKE_USE_LIBBPF_PACKAGE=ON \
    -DENABLE_LLVM_SHARED=ON \
    -DENABLE_CLANG_JIT=ON \
    -DPY_SKIP_DEB_LAYOUT=ON \
    -DPYTHON_CMD=${PYTHON} \
    -DPYTHON_FLAGS=--install-lib=${PYTHON_SITEPACKAGES_DIR} \
"

# Avoid stripping debuginfo.so to fix some tests.
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

do_install:append() {
        sed -e 's@#!/usr/bin/env python@#!/usr/bin/env python3@g' \
            -i $(find ${D}${datadir}/${PN} -type f)
        sed -e 's@#!/usr/bin/python.*@#!/usr/bin/env python3@g' \
            -i $(find ${D}${datadir}/${PN} -type f)
        rm -rf ${D}${datadir}/bcc/examples/lua
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests/cc
    # ptest searches for shared libs and archive files in the build folder.
    # Hence, these files are copied to the image to fix these tests.
    install -d ${D}${B}/tests/cc
    install ${B}/tests/cc/archive.zip ${B}/tests/cc/libdebuginfo_test_lib.so ${B}/tests/cc/with_gnu_debuglink.so ${B}/tests/cc/with_gnu_debugdata.so ${B}/tests/cc/debuginfo.so ${D}${B}/tests/cc
    install -d ${D}/opt
    install ${B}/tests/cc/test_libbcc_no_libbpf ${B}/tests/cc/libusdt_test_lib.so ${D}${PTEST_PATH}/tests/cc
    cp -rf ${S}/tests/python ${D}${PTEST_PATH}/tests/python
    install ${UNPACKDIR}/ptest_wrapper.sh ${D}${PTEST_PATH}/tests
    install ${S}/examples/networking/simulation.py ${D}${PTEST_PATH}/tests/python
    find ${S}/tools/ -type f -name "*.py" -exec \
    sed -i \
    -e 's@^#! */usr/bin/env python$@#!/usr/bin/env python3@' \
    -e 's@^#! */usr/bin/python.*@#!/usr/bin/env python3@' {} +
    cp -rf ${S}/tools/ ${D}${PTEST_PATH}/../../tools/
}

FILES:${PN} += "${PYTHON_SITEPACKAGES_DIR}"
FILES:${PN} += "${B}/tests/cc"
FILES:${PN}-ptest += "${libdir}/libbcc.so"
FILES:${PN}-ptest += "${libdir}/tools/"
FILES:${PN}-ptest += "/opt/"
FILES:${PN}-doc += "${datadir}/${PN}/man"

COMPATIBLE_HOST = "(x86_64.*|aarch64.*|powerpc64.*|riscv64.*)-linux"

# WARNING: bcc-0.30.0+git-r0 do_package_qa: QA Issue: File /usr/lib/bcc/ptest/tests/cc/test_libbcc_no_libbpf in package bcc-ptest contains reference to TMPDIR [buildpaths]
# this one is difficult to resolve, because the tests use CMAKE_CURRENT_BINARY_DIR directly in .cc e.g.:
# https://github.com/iovisor/bcc/commit/7271bfc946a19413761be2e3c60c48bf72c5eea1#diff-233a0bfa490f3d7466c49935b64c86dd93956bbc0461f5af703b344cf6601461
# we would probably need to use separate variable for "runtime" path for test assets from the standard CMAKE_CURRENT_BINARY_DIR variable or use relative
# path from the test binary
WARN_QA:append = "${@bb.utils.contains('PTEST_ENABLED', '1', ' buildpaths', '', d)}"
ERROR_QA:remove = "${@bb.utils.contains('PTEST_ENABLED', '1', 'buildpaths', '', d)}"
