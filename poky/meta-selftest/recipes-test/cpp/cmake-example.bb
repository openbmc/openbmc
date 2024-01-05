#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "A C++ example compiled with cmake."

require cpp-example.inc

SRC_URI += "file://CMakeLists.txt"

inherit cmake-qemu

PACKAGECONFIG[failing_test] = "-DFAILING_TEST=ON"

FILES:${PN}-ptest += "${bindir}/test-cmake-example"

do_run_tests () {
    bbnote ${DESTDIR:+DESTDIR=${DESTDIR} }${CMAKE_VERBOSE} cmake --build '${B}' --target test -- ${EXTRA_OECMAKE_BUILD}
    eval ${DESTDIR:+DESTDIR=${DESTDIR} }${CMAKE_VERBOSE} cmake --build '${B}' --target test -- ${EXTRA_OECMAKE_BUILD}
}
do_run_tests[doc] = "Run cmake --target=test using qemu-user"

addtask do_run_tests after do_compile
