SUMMARY = "Host SDK package for Rust cross canadian toolchain"
PN = "packagegroup-rust-cross-canadian-${MACHINE}"

inherit cross-canadian packagegroup

PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

RUST="rust-cross-canadian-${TRANSLATED_TARGET_ARCH}"
CARGO="cargo-cross-canadian-${TRANSLATED_TARGET_ARCH}"
RUST_TOOLS="rust-tools-cross-canadian-${TRANSLATED_TARGET_ARCH}"

RDEPENDS:${PN} = " \
    ${@all_multilib_tune_values(d, 'RUST')} \
    ${@all_multilib_tune_values(d, 'CARGO')} \
    rust-cross-canadian-src \
    ${@all_multilib_tune_values(d, 'RUST_TOOLS')} \
"

