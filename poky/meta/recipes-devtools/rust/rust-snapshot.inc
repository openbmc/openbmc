## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.txt
## TODO: find a way to add additional SRC_URIs based on the contents of an
##       earlier SRC_URI.
RS_VERSION = "1.57.0"
CARGO_VERSION = "1.57.0"

# TODO: Add hashes for other architecture toolchains as well. Make a script?
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "ccdc72d06c90841b7227ec3630337881eeda3da5fbe87328d2e9d705f6f8016c"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "c42f40c5279a3a9d539cae93dbcab9d333777ad9a60a5b9f1086b874ef63db86"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "ed2013713ae742895af5df8d91c5430ba9ba3c781e3bc7f3471b220cc06d565d"

SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "3618da916a0f92f241cf1d67d04bb57835b303cf2047b57dc2f2487b89a4fc1f"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "f26811e48d03c56c125de03d389e1ae7c6df36990953c1670c6a5676bc12d4cb"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "6d11cd94618d80cda273eeeae7285980445f61a49ebacc616777b482a41cbf3f"

SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "fc07eb3e9f3d227428cc5b53ca868e3de375bc198ce4dce7b87a9246e6fec81a"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "f43cb99109c3438c77c7079cdce4673df3320e310158e0b4d949c1babc4300fc"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "599cf1b5a8cdbf76d591621bc9222aefa60e2f5fd378ae71c4dcf4514c47122e"

SRC_URI += " \
    https://static.rust-lang.org/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_STD_SNAPSHOT = "rust-std-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${CARGO_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
