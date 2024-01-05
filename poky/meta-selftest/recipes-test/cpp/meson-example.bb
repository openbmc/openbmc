#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "A C++ example compiled with meson."

require cpp-example.inc

SRC_URI += "\
    file://meson.build \
    file://meson.options \
"

inherit pkgconfig meson

PACKAGECONFIG[failing_test] = "-DFAILING_TEST=enabled"

FILES:${PN}-ptest += "${bindir}/test-mesonex"

do_run_tests () {
    meson test -C "${B}" --no-rebuild
}
do_run_tests[doc] = "Run meson test using qemu-user"

addtask do_run_tests after do_compile
