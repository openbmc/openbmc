#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

##
## Purpose:
## This class is used by any recipes that want to compile a C ABI compatible
## library with header and pkg config file

inherit cargo_common pkgconfig

# We need cargo-c to compile for the target
BASEDEPENDS:append = " cargo-c-native"

B = "${WORKDIR}/build"

CARGO_BUILD_FLAGS += "\
    --destdir ${D} \
    --prefix ${prefix} \
    --libdir ${libdir} \
    --library-type cdylib \
    "

do_compile[progress] = "outof:\s+(\d+)/(\d+)"
cargo_c_do_compile() {
    oe_cargo_fix_env
    export RUSTFLAGS="${RUSTFLAGS}"
    bbnote "Using rust targets from ${RUST_TARGET_PATH}"
    cargo-cbuild cbuild ${CARGO_BUILD_FLAGS}
}

cargo_c_do_install() {
    oe_cargo_fix_env
    export RUSTFLAGS="${RUSTFLAGS}"
    cargo-cinstall cinstall ${CARGO_BUILD_FLAGS}
}

EXPORT_FUNCTIONS do_compile do_install
