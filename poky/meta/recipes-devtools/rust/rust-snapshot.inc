## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.txt
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.
## TODO: find a way to add additional SRC_URIs based on the contents of an
##       earlier SRC_URI.
RS_VERSION = "1.61.0"
CARGO_VERSION = "1.61.0"

# TODO: Add hashes for other architecture toolchains as well. Make a script?
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "270b07aa5f2de52255a117e1e587138d77375ce0d09a1d7fead085f29b3977e9"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "21c4613f389ed130fbaaf88f1e984319f72b5fc10734569a5ba19e22ebb03abd"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "9461727d754f865ef2a87479d40bbe4c5176f80963b7c50b7797bc8940d7a0a0"

SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "57d60a519dbce12146849f7e72d55f3cffe9cdcbff8d58e90bb62d3c016bb5c0"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "c996de6391e3ea94629fbc09b03bce186fcde345159f43ec95a82c500adb5e94"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "a055e6cfd9b5f8938780db6179d2ef92990c714ce64278337d7edf3d29c8ab62"

SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "36c0ccff14c80419507561db050f9533f0abd43fc50f3ddb859c10df74b1c351"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "dc54893d747e4f3330515caa75e404f78c6c5475a1216d1428f5e7ce1c2e9602"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "09817011ff1ef4b7006387c7cabb6a059731792a9372533dec7d87e7f014444b"

SRC_URI += " \
    https://static.rust-lang.org/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    https://static.rust-lang.org/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_STD_SNAPSHOT = "rust-std-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${RS_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${CARGO_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
