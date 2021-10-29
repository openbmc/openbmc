## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.txt
## TODO: find a way to add additional SRC_URIs based on the contents of an
##       earlier SRC_URI.
RS_VERSION = "1.56.0"
CARGO_VERSION = "1.56.0"

# TODO: Add hashes for other architecture toolchains as well. Make a script?
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "f693e4f41a959d62528e0043b0b010c4815fb9f5887267ae34ff8b860f8ec4ae"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "2a5f919e2036496c02ab4707a5eb8bd8a4ce6ea589e01cb39f25d213a26dcdfe"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "f972125810d2db7df2e0af56d9f6a3dcdacc52d983bdf0ca17309b45fc44209b"

SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "3cd1c69c618e5efaf578a156c2a950c93964f9b47ba4d705d5700bdc64880231"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "0d2491a59f697d2ea52fc429b8d4665acb5f79b0e16703f3604b61b8aa68d1c4"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "9d97cd655845572846c058577f68ea74b6111f8ea7649db308f786475f764a09"

SRC_URI += " \
    https://static.rust-lang.org/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_STD_SNAPSHOT = "rust-std-${RS_VERSION}-${BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${RS_VERSION}-${BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${CARGO_VERSION}-${BUILD_ARCH}-unknown-linux-gnu"
