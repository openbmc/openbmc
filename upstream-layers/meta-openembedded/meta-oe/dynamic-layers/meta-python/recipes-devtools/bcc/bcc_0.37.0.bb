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
            xz \
            "

RDEPENDS:${PN} += "bash python3 python3-core python3-setuptools xz"
RDEPENDS:${PN}-ptest = "kernel-devsrc packagegroup-core-buildessential cmake bash python3 python3-netaddr python3-pyroute2"

SRC_URI = "gitsm://github.com/iovisor/bcc;branch=master;protocol=https;lfs=0;tag=v${PV} \
           file://0001-CMakeLists.txt-override-the-PY_CMD_ESCAPED.patch \
           file://0001-Vendor-just-enough-extra-headers-to-allow-libbpf-to-.patch \
           file://0001-tests-cc-Allow-overriding-the-runtime-location-of-te.patch \
           file://0001-src-cc-Fix-MCContext-constructor-for-LLVM-23.patch \
           file://run-ptest \
           file://ptest_wrapper.sh \
           "

SRCREV = "306a2819f73d9525430693e6399d58caf6e12b3b"

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

# The C tests hardcode the location of their assets at build time. Point that at
# where do_install_ptest puts them so the build directory does not end up baked
# into the installed test binaries.
EXTRA_OECMAKE += "-DTEST_ASSET_DIR=${PTEST_PATH}/tests/cc"

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
    # The C tests look their assets up under TEST_ASSET_DIR, which is pointed at
    # ${PTEST_PATH}/tests/cc at configure time, so install them next to the test
    # binaries rather than at the (build host specific) build directory path.
    install ${B}/tests/cc/archive.zip ${B}/tests/cc/libdebuginfo_test_lib.so ${B}/tests/cc/with_gnu_debuglink.so ${B}/tests/cc/with_gnu_debugdata.so ${B}/tests/cc/debuginfo.so ${D}${PTEST_PATH}/tests/cc
    install -m 0644 ${B}/tests/cc/dummy_proc_map.txt ${D}${PTEST_PATH}/tests/cc
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
FILES:${PN}-ptest += "${libdir}/libbcc.so"
FILES:${PN}-ptest += "${libdir}/tools/"
FILES:${PN}-ptest += "/opt/"
FILES:${PN}-doc += "${datadir}/${PN}/man"

COMPATIBLE_HOST = "(x86_64.*|aarch64.*|powerpc64.*|riscv64.*)-linux"
