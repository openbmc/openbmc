## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.
## TODO: find a way to add additional SRC_URIs based on the contents of an
##       earlier SRC_URI.

SNAPSHOT_VERSION = "1.65.0"

# TODO: Add hashes for other architecture toolchains as well. Make a script?
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "2b588cd2d49688c0c33b7466614123e8fe4c910f4d802fc0ff0662b1772816a9"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "62b89786e195fc5a8a262f83118d6689832b24228c9d303cba8ac14dc1e9adc8"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "82547aacaf42fc3c2970ec31b96751dfbeba3dffe1a042a3780bd670c29a89bf"

SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "735b681c8a6e60925c76d6cc899e78b4cb4562ada24a1f265b2021c1faad78ad"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "67c3d3545fd898c1383071c0f6296453565e0da10903c50652d7bf679b53e8a2"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "3fd483c0d58673ab69862824408c8a48612827ddcdeaaca0f8fbe5ca02214a4c"

SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "ce18b44300f7d5d94856cef5b270ba010061fafa411beb9782207e26cbab88a6"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "a6ce7aadd10a3fd84fe4717a59378421a65b101b61f27eed8b09336b8daf62cf"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "9393de910df7cd6947e380460a1144ac2373a36c776c7367a81212a51a92d9a7"

SRC_URI += " \
    https://static.rust-lang.org/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
