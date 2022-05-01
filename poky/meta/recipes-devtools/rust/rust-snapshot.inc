## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.txt
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.
## TODO: find a way to add additional SRC_URIs based on the contents of an
##       earlier SRC_URI.
RS_VERSION = "1.59.0"
CARGO_VERSION = "1.59.0"

# TODO: Add hashes for other architecture toolchains as well. Make a script?
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "c854a9ee3dd8e5be9522c1581f75838c1cbae6dece3934b0004f138c4a5024a3"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "838de1fef855ef7733a87862c2575e8da9f3fa11fd0a8ce05c293038ea92356e"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "f56ebfb333ea46e4429377bf4b16a2ec889d61640a41c3093577cdd8f3c80b96"

SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "68e50dee4f6dddeab7330906e46022f57f2c004c847eae3f5b1bc82c59e43fc0"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "d9789013ef6edd76eae3e7427b48f420a036ab3ee2af883e60baa33a9e1c23d7"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "11b8da4b90ff74c6c796a3d6c1f5150de23c411ed2546e10b301077b904191fd"

SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "15dee7705967e3351aff11c64ada30c4957b54066c7ee49b87be4b8155bd0a7d"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "e5458f55e51f8baecab4ff1e43fae5c6e1c40d8171098ab633747ee0684b37bb"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "fdab44481286db3ea8f4b6d409e648355009ef458188352d5e2c5799f7614fbd"

SRC_URI += " \
    https://static.rust-lang.org/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_STD_SNAPSHOT = "rust-std-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${CARGO_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
