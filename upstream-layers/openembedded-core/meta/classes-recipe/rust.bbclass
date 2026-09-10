#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit rust-common

RUSTC = "rustc"

def rust_base_dep(d):
    # Taken from meta/classes/base.bbclass `base_dep_prepend` and modified to
    # use rust instead of gcc
    deps = ""
    if not d.getVar('INHIBIT_DEFAULT_RUST_DEPS'):
        if (d.getVar('HOST_SYS') != d.getVar('BUILD_SYS')):
            deps += " rust-native ${RUSTLIB_DEP}"
        else:
            deps += " rust-native"
    return deps

DEPENDS:append = " ${@rust_base_dep(d)}"

rustlib_suffix = "${TUNE_ARCH}${TARGET_VENDOR}-${TARGET_OS}/rustlib/${RUST_HOST_SYS}/lib"
# Native sysroot standard library path
rustlib_src = "${prefix}/lib/${rustlib_suffix}"
# Host sysroot standard library path
rustlib = "${libdir}/${rustlib_suffix}"
rustlib:class-native = "${libdir}/rustlib/${BUILD_SYS}/lib"
