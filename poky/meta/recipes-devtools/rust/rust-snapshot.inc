## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.
## TODO: find a way to add additional SRC_URIs based on the contents of an
##       earlier SRC_URI.
RS_VERSION = "1.62.0"
CARGO_VERSION = "1.62.0"

# TODO: Add hashes for other architecture toolchains as well. Make a script?
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "addfae87b6b1b521d98a50fdc5120990888a51bb397100062e9c558267c67c77"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "e7f71f4ef09334ddc9ec8cbf2f958d654e36f580c95f8fec6d5c816ce256dbd6"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "815c63119a9cf0282ff240c6444b6f867238763ee3dea182f10837ae7dbbb1d4"

SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "dd5df8a92af3e5d49a1122b9561821ebd72a9317884a37ecddae041e652a7563"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "0fa320a19d41dcfc592bc006f5e9eda8e3b972598a26c96ad64eedd868516df3"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "475038ecacca9ff586cad2082d5d950544b0d581a2a287facc7d899aae488813"

SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "d6678b7c971f3adbe7f820adae669d03a314468441e2907747c76eca98e0be92"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "b66d0bc6dbfdc0d4b826f787ec4e772dea8e3d2015cecbe2105632d468c28dcb"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "016257f1641693008068bd086fec66d68550d1778f6aea9d06c9b263fca392d5"

SRC_URI += " \
    https://static.rust-lang.org/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_STD_SNAPSHOT = "rust-std-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${CARGO_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
