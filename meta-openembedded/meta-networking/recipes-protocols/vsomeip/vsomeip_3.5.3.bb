SUMMARY = "The implementation of SOME/IP"
DESCRIPTION = "The vsomeip stack implements the http://some-ip.com/ \
(Scalable service-Oriented MiddlewarE over IP (SOME/IP)) protocol."
HOMEPAGE = "https://github.com/COVESA/vsomeip"
SECTION = "net"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

GTEST_VER = "1.15.2"
SRC_URI = "git://github.com/GENIVI/${BPN}.git;branch=master;protocol=https;name=vsomeip \
           https://github.com/google/googletest/releases/download/v${GTEST_VER}/googletest-${GTEST_VER}.tar.gz;name=gtest;subdir=git/ \
           file://0001-Fix-pkgconfig-dir-for-multilib.patch \
           file://0002-Install-example-configuration-files-to-etc-vsomeip.patch \
           file://0004-Do-not-specify-PIE-flag-explicitly.patch \
           file://0005-test-common-CMakeLists.txt-add-missing-link-with-dlt.patch \
          "

SRCREV = "6461369b3874c844642c9adaac9d1b7406794ab8"
SRC_URI[gtest.sha256sum] = "7b42b4d6ed48810c5362c265a17faebe90dc2373c885e5216439d37927f02926"

COMPATIBLE_HOST:mips = "null"
COMPATIBLE_HOST:mips64 = "null"
COMPATIBLE_HOST:powerpc = "null"
COMPATIBLE_HOST:libc-musl = 'null'

DEPENDS = "boost dlt-daemon googletest"

S = "${WORKDIR}/git"

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

RDEPENDS:${PN}-test = "bash lsof"

do_compile:append() {
    cmake_runcmake_build --target examples
    cmake_runcmake_build --target build_tests
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
    install -m 0755 ${B}/test/common/libvsomeip_utilities.so \
        ${D}/opt/${PN}-test/test/test/common/

    for d in unit_tests network_tests; do
        install -d ${D}/opt/${PN}-test/test/$d
        cp -rf ${B}/test/$d/*_tests ${D}/opt/${PN}-test/test/$d
    done
}

PACKAGES += "${PN}-test"

FILES:${PN}-dbg += " \
   /opt/${PN}-test/.debug/* \
   "
FILES:${PN}-test = " \
   /opt/${PN}-test \
   "
SKIP_RECIPE[vsomeip] ?= "Does not work with boost >= 1.87"
