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
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=enabled,-DWITH_SYSTEMD=disabled"

# Support installing all recipes variants in parallel
EXTRA_OEMESON += "\
    -DBINARY_NAME=${EX_BINARY_NAME} \
    -DTEST_BINARY_NAME=${EX_TEST_BINARY_NAME} \
    -DCONFIG_FILE_NAME=${BPN}.conf \
"

FILES:${PN}-ptest += "${bindir}/${EX_TEST_BINARY_NAME}"

do_run_tests () {
    meson test -C "${B}" --no-rebuild
}
do_run_tests[doc] = "Run meson test using qemu-user"

addtask do_run_tests after do_compile

EX_BINARY_NAME = "mesonex"
