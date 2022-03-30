## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.txt
## TODO: find a way to add additional SRC_URIs based on the contents of an
##       earlier SRC_URI.
RS_VERSION = "1.58.0"
CARGO_VERSION = "1.58.0"

# TODO: Add hashes for other architecture toolchains as well. Make a script?
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "319e2dc5f50cbdfb7091f56643c637465d6bc34291ccdaf1a06a2023a37f50c7"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "47e586451ac25027eb6c0d23c881a917d21d074d2fe9e5a3f41b4b6de1622be0"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "3d44be4cf353f4172b79485121286be667b76246d9998e7c48a3c2907f5e9552"

SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "b562646864cea55079e4b9d35dc2e9b6abc8efa224e2e49779ba2cbf8ff83b3d"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "8d7c8a64118ee523ad3ffc84baf91cf02ff4415390dc835f3925f8697170ec65"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "0dd38e1c0217fec9a4075c74e3faa4ab5aeffe966f93e6ec56462b4df192c8b0"

SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "aebb2dcd1ec692997ac0f7f7420b7516fd914ec992449e6c53c22b45cd456f08"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "611a70f459936cda2b4d13046a34f7badc9628901b90752be392723c25a1f7ef"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "176b8899b031b9c96bef290933683a2dab365c623537984954e0a63a1a388cd7"

SRC_URI += " \
    https://static.rust-lang.org/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_STD_SNAPSHOT = "rust-std-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${CARGO_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
