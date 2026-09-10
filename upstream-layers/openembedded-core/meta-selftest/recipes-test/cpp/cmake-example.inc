#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

require cpp-example.inc

SRC_URI += "file://CMakeLists.txt"

inherit cmake-qemu

PACKAGECONFIG[failing_test] = "-DFAILING_TEST=ON"
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=ON,-DWITH_SYSTEMD=OFF"

# Support installing all recipe variants in parallel
EXTRA_OECMAKE += "\
    -DBINARY_NAME=${EX_BINARY_NAME} \
    -DTEST_BINARY_NAME=${EX_TEST_BINARY_NAME} \
"

FILES:${PN}-ptest += "${bindir}/${EX_TEST_BINARY_NAME}"

do_run_tests () {
    bbnote ${DESTDIR:+DESTDIR=${DESTDIR} }${CMAKE_VERBOSE} cmake --build '${B}' --target test -- ${EXTRA_OECMAKE_BUILD}
    eval ${DESTDIR:+DESTDIR=${DESTDIR} }${CMAKE_VERBOSE} cmake --build '${B}' --target test -- ${EXTRA_OECMAKE_BUILD}
}
do_run_tests[doc] = "Run cmake --target=test using qemu-user"

addtask do_run_tests after do_compile
