## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.
## TODO: find a way to add additional SRC_URIs based on the contents of an
##       earlier SRC_URI.
RS_VERSION = "1.64.0"
CARGO_VERSION = "1.64.0"

# TODO: Add hashes for other architecture toolchains as well. Make a script?
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "6ace34bcbba9557aa2fc3a0515c3da4a83ca24d7d45506c5e1b32f589fa38a8b"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "1f5756a03119853b53358018c5b1592940a2354c3c9f84ee7faf684e3478f8f0"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "21434d83a30ad3fa4e4831487c5574a20b07dd57d213b26e1246290c57d4ec41"

SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "40abc9ec4f86ff0e37ba176e4c68dfa708e9857bb0372169c865367593127566"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "8f10b379bcc8caaab983b7d04a3f105dae42f95718f231b46d7e68685d239191"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "103d69e4f2f49f5a6309d7d195747b31f94cf707f90fb49cf76039a5ce6295c7"

SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "ef697469b2a3ea8897f49b70e3be0c7aaca3f26fd3234812113e2e85cafac738"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "11630fc51fffe722e52f649357b5948c24b5305cfb61a8114527234e054451c4"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "ba7188b2c7890e61bf58d3aa9e94c323fec375f67cf03841bbcc0f6c800fe6ad"

SRC_URI += " \
    https://static.rust-lang.org/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_STD_SNAPSHOT = "rust-std-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${CARGO_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
