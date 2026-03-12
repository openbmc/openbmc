SUMMARY = "bpftrace"
HOMEPAGE = "https://github.com/iovisor/bpftrace"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "bison-native \
            flex-native \
            gzip-native \
            elfutils \
            bpftool-native \
            bcc \
            libcereal \
            libbpf \
            xxd-native \
            "
DEPENDS += "${@bb.utils.contains('PTEST_ENABLED', '1', 'pahole-native llvm-native', '', d)}"

RDEPENDS:${PN} += "bash python3 xz"
RDEPENDS:${PN}-ptest += "bpftool"

SRC_URI = "git://github.com/iovisor/bpftrace;branch=release/0.24.x;protocol=https;tag=v${PV} \
           file://run-ptest \
           file://0002-CMakeLists.txt-allow-to-set-BISON_FLAGS-like-l.patch \
"
SRCREV = "4c1f02a43f993758d445952ccd96e552752defec"

inherit bash-completion cmake ptest pkgconfig

PACKAGECONFIG ?= " \
        ${@bb.utils.contains('PTEST_ENABLED', '1', 'tests', '', d)} \
        ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "systemd", "", d)} \
        "

PACKAGECONFIG[tests] = "-DBUILD_TESTING=ON,-DBUILD_TESTING=OFF,gtest"
PACKAGECONFIG[systemd] = "-DENABLE_SYSTEMD=ON,-DENABLE_SYSTEMD=OFF,systemd"

do_install_ptest() {
    if [ -e ${B}/tests/bpftrace_test ]; then
        install -Dm 755 ${B}/tests/bpftrace_test ${D}${PTEST_PATH}/tests/bpftrace_test
        cp -rf ${B}/tests/runtime ${D}${PTEST_PATH}/tests
        cp -rf ${B}/tests/test* ${D}${PTEST_PATH}/tests
    fi
    for f in testlibs/cmake_install.cmake \
    testprogs/cmake_install.cmake \
    testlibs/CTestTestfile.cmake \
    testprogs/CTestTestfile.cmake
    do
        sed -i -e 's|${STAGING_BINDIR_TOOLCHAIN}/||' ${D}${libdir}/bpftrace/ptest/tests/$f
        sed -i -e 's|${S}/||' ${D}${libdir}/bpftrace/ptest/tests/$f
        sed -i -e 's|${B}/||' ${D}${libdir}/bpftrace/ptest/tests/$f
    done
}

EXTRA_OECMAKE = " \
    -DCMAKE_ENABLE_EXPORTS=1 \
    -DCMAKE_BUILD_TYPE=Release \
    -DUSE_SYSTEM_BPF_BCC=ON \
    -DENABLE_MAN=OFF \
    -DBISON_FLAGS='--file-prefix-map=${WORKDIR}=' \
"

COMPATIBLE_HOST = "(x86_64.*|aarch64.*|powerpc64.*|riscv64.*)-linux"

INHIBIT_PACKAGE_STRIP_FILES += "\
    ${PKGD}${PTEST_PATH}/tests/testprogs/uprobe_test \
"

WARN_QA:append = "${@bb.utils.contains('PTEST_ENABLED', '1', ' buildpaths', '', d)}"
ERROR_QA:remove = "${@bb.utils.contains('PTEST_ENABLED', '1', 'buildpaths', '', d)}"
