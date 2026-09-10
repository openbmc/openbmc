#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "A C++ example compiled with cmake and clang."

require cmake-example.inc

TOOLCHAIN = "clang"
EX_BINARY_NAME = "${BPN}"
EX_SERVICE_USER = "cmake-example"
