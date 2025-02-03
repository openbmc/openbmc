SUMMARY = "Host SDK package for Rust cross canadian toolchain"
PN = "packagegroup-rust-cross-canadian-${MACHINE}"

inherit cross-canadian packagegroup

PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

RUST = "rust-cross-canadian-${TRANSLATED_TARGET_ARCH}"

RDEPENDS:${PN} = " \
    ${@all_multilib_tune_values(d, 'RUST')} \
    nativesdk-binutils \
    nativesdk-gcc \
    nativesdk-glibc-dev \
    nativesdk-libgcc-dev \
    nativesdk-rust \
    nativesdk-cargo \
    nativesdk-rust-tools-clippy \
    nativesdk-rust-tools-rustfmt \
"

