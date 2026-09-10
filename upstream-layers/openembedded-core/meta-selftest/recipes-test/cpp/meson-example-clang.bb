#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "A C++ example compiled with meson and clang."

require meson-example.inc

TOOLCHAIN = "clang"
EX_BINARY_NAME = "mesonex-clang"
EX_SERVICE_USER = "meson-example"
