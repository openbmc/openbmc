SUMMARY = "The implementation of SOME/IP"
DESCRIPTION = "The vsomeip stack implements the http://some-ip.com/ \
(Scalable service-Oriented MiddlewarE over IP (SOME/IP)) protocol."
HOMEPAGE = "https://github.com/COVESA/vsomeip"
SECTION = "net"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

SRC_URI = "git://github.com/GENIVI/${BPN}.git;branch=master;protocol=https;name=vsomeip \
           file://0001-Fix-pkgconfig-dir-for-multilib.patch \
           file://0002-Install-example-configuration-files-to-etc-vsomeip.patch \
           file://0003-Do-not-build-external-gtest.patch \
           file://0004-Do-not-specify-PIE-flag-explicitly.patch \
           file://0005-test-common-CMakeLists.txt-add-missing-link-with-dlt.patch \
          "

SRCREV = "02c199dff8aba814beebe3ca417fd991058fe90c"

COMPATIBLE_HOST:mips = "null"
COMPATIBLE_HOST:mips64 = "null"
COMPATIBLE_HOST:powerpc = "null"
COMPATIBLE_HOST:libc-musl = 'null'

DEPENDS = "boost dlt-daemon googletest"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DINSTALL_LIB_DIR:PATH=${baselib} \
                 -DINSTALL_CMAKE_DIR:PATH=${baselib}/cmake/vsomeip3 \
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
        cp -f ${B}/test/$d/*test* ${D}/opt/${PN}-test/test/$d
    done
}

PACKAGES += "${PN}-test"

FILES:${PN}-dbg += " \
   /opt/${PN}-test/.debug/* \
   "
FILES:${PN}-test = " \
   /opt/${PN}-test \
   "
