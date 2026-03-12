SUMMARY = "The implementation of SOME/IP"
DESCRIPTION = "The vsomeip stack implements the http://some-ip.com/ \
(Scalable service-Oriented MiddlewarE over IP (SOME/IP)) protocol."
HOMEPAGE = "https://github.com/COVESA/vsomeip"
SECTION = "net"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

GTEST_VER = "1.17.0"
SRC_URI = "git://github.com/GENIVI/${BPN}.git;branch=master;protocol=https;name=vsomeip \
           https://github.com/google/googletest/releases/download/v${GTEST_VER}/googletest-${GTEST_VER}.tar.gz;name=gtest;subdir=${BB_GIT_DEFAULT_DESTSUFFIX}/ \
           file://0001-Fix-pkgconfig-dir-for-multilib.patch \
           file://0002-Install-example-configuration-files-to-etc-vsomeip.patch \
           file://0003-Do-not-specify-PIE-flag-explicitly.patch \
           file://0004-Fix-build-with-boost-1.89.patch \
           file://0005-Replace-address-from_string-with-make_address.patch \
           file://0006-Fix-scanning-64-bit-integer-types.patch \
           file://0007-Do-not-treat-warnings-as-errors-with-clang.patch \
           file://0008-Replace-io_service-with-io_context.patch \
           file://0009-cached_event_tests-CMakeLists.txt-update-cmake_minim.patch \
          "

SRCREV = "f58ba578c8c04e02dcf08d3ebcb9a71ca1e203ea"
SRC_URI[gtest.sha256sum] = "65fab701d9829d38cb77c14acdc431d2108bfdbf8979e40eb8ae567edf10b27c"

COMPATIBLE_HOST:mips = "null"
COMPATIBLE_HOST:mips64 = "null"
COMPATIBLE_HOST:powerpc = "null"
COMPATIBLE_HOST:libc-musl = 'null'

DEPENDS = "boost dlt-daemon"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DINSTALL_LIB_DIR:PATH=${baselib} \
                 -DINSTALL_CMAKE_DIR:PATH=${baselib}/cmake/vsomeip3 \
                 -DGTEST_ROOT=${S}/googletest-${GTEST_VER} \
                "

# For vsomeip-test
EXTRA_OECMAKE += "-DTEST_IP_MASTER=10.0.3.1 \
                  -DTEST_IP_SLAVE=10.0.3.2 \
                  -DTEST_IP_SLAVE_SECOND=10.0.3.3 \
                  -DTEST_UID=1000 -DTEST_GID=1000 \
                 "
# Fixes build with boost 1.90+
CXXFLAGS:append = " -Wno-error=deprecated-declarations"

RDEPENDS:${PN}-test = "bash lsof"

OECMAKE_TARGET_COMPILE += "vsomeip_ctrl examples build_tests"

do_compile:prepend() {
    sed -i -e 's#${S}/build#/opt/${PN}-test#g' ${S}/test/unit_tests/security_policy_manager_impl_tests/policy_manager_impl_unit_test_macro.hpp
}

do_install:append() {
    install -d ${D}/opt/${PN}-test/examples
    install -m 0755 ${B}/examples/*-sample ${D}/opt/${PN}-test/examples
    install -d ${D}/opt/${PN}-test/examples/routingmanagerd
    install -m 0755 ${B}/examples/routingmanagerd/routingmanagerd \
        ${D}/opt/${PN}-test/examples/routingmanagerd

    install -d ${D}/opt/${PN}-test/test/test/common
    cp -rf ${S}/test/common/examples_policies \
        ${D}/opt/${PN}-test/test/test/common/

    install -d ${D}/opt/${PN}-test/test/common
    install -m 0755 ${B}/test/common/libvsomeip_utilities.so \
        ${D}/opt/${PN}-test/test/common/

    for d in unit_tests network_tests; do
        install -d ${D}/opt/${PN}-test/test/$d
        cp -rf ${B}/test/$d/*_tests ${D}/opt/${PN}-test/test/$d
        find ${D}/opt/${PN}-test/test/$d -maxdepth 2 \( -name "*.cmake" -o -name "CMakeFiles" \) -exec rm -rf {} \;
    done
    sed -i -e 's#../..${B}#/opt/${PN}-test#g' ${D}/opt/${PN}-test/test/network_tests/lazy_load_tests/vsomeip/vsomeip_policy_extensions.json
}

PACKAGES += "${PN}-test"

FILES:${PN}-dbg += " \
   /opt/${PN}-test/.debug/* \
   "
FILES:${PN}-test = " \
   /opt/${PN}-test \
   "

INSANE_SKIP += "32bit-time"
