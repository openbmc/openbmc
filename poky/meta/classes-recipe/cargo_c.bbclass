#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

##
## Purpose:
## This class is used by any recipes that want to compile a C ABI compatible
## library with header and pkg config file

inherit cargo pkgconfig

# the binaries we will use
CARGO_C_BUILD = "cargo-cbuild"
CARGO_C_INSTALL = "cargo-cinstall"

# We need cargo-c to compile for the target
BASEDEPENDS:append = " cargo-c-native"

do_compile[progress] = "outof:\s+(\d+)/(\d+)"
cargo_c_do_compile() {
    oe_cargo_fix_env
    export RUSTFLAGS="${RUSTFLAGS}"
    bbnote "Using rust targets from ${RUST_TARGET_PATH}"
    bbnote "cargo-cbuild = $(which ${CARGO_C_BUILD})"
    bbnote "${CARGO_C_BUILD} cbuild ${CARGO_BUILD_FLAGS}"
    "${CARGO_C_BUILD}" cbuild ${CARGO_BUILD_FLAGS}
}

cargo_c_do_install() {
    oe_cargo_fix_env
    export RUSTFLAGS="${RUSTFLAGS}"
    bbnote "cargo-cinstall = $(which ${CARGO_C_INSTALL})"
    "${CARGO_C_INSTALL}" cinstall ${CARGO_BUILD_FLAGS} \
        --destdir ${D} \
        --prefix ${prefix} \
        --library-type cdylib
}

EXPORT_FUNCTIONS do_compile do_install
