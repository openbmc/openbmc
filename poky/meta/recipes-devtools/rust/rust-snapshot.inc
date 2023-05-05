## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.68.2"

SRC_URI[cargo-snapshot-aarch64.sha256sum] = "a1b1f8ffdd56747453cb9531f70474ddbe603533e7b06647a810e32fc7c3a8ba"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "cbe60945af743804be6822704f986c812dd1fe1d167602aec5350a8f519f9861"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "d78579af4d4b98b3bf49b8a0b7848960e68254150ac517faeb9caf7b9f44ede2"

SRC_URI[cargo-snapshot-i686.sha256sum] = "aeee38c0cd35b531d9f00028f720358256e9f3b80b176467c6afc033a16c92aa"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "695d4469c131b9993c939db0ce73a3866ab8e69dd657ec3137f3d594857f7812"
SRC_URI[rustc-snapshot-i686.sha256sum] = "dc533e7d0f0349d92a98973e20b99d69b09a25b8675f28a38fa26b0160615fd4"

SRC_URI[cargo-snapshot-mips.sha256sum] = "8d24614f31ba67fe10a642e9f49d64066b4ce6789a80a101ae10afcc2a194c97"
SRC_URI[rust-std-snapshot-mips.sha256sum] = "4ab007f223cf723f8f7adf26a6014f4f5aa444f88c19daccc5ac81ea30275c66"
SRC_URI[rustc-snapshot-mips.sha256sum] = "68c6aac6e0e95588b65d6f937032b57ad2a09b2897990a3a930729763f728e12"

SRC_URI[cargo-snapshot-mipsel.sha256sum] = "8664ff821dfd8bb252c61c2b7473e355bd38cd8a6bc15a9ecc98af46f6a2b282"
SRC_URI[rust-std-snapshot-mipsel.sha256sum] = "c299d65b3594c309d092f6a0fe15c205ab489ffa50910fb8126e944a2727cbe5"
SRC_URI[rustc-snapshot-mipsel.sha256sum] = "de72245d5e6db0e4327ce105a7ce3d5597a3792e71b95578dd7ec62af56946fb"

SRC_URI[cargo-snapshot-powerpc.sha256sum] = "13ad9e74376fc01d9d770447c141a18dd986dd60bbd4c6177ed80df4a28fc142"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "e75bdc8a6e3f00285a37606240862cdad69a986c3e3d47d6af1376613281233a"
SRC_URI[rustc-snapshot-powerpc.sha256sum] = "92b08b1ad69786f3bcc6fd7bb71411ad162dca478f9636027bf809b9de6eea72"

SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "ad4eded41be32bb19a5f6224daacb9589947956f4be9f884f7ea06323b448088"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "354322352cd8b661b84f5e97729d5d7adb7df9cb9bfd43cac378271c40214d7f"
SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "a23e71c7da172ece7ef760db043b010c84dd8a491cd9c00303e2c01ad521dec2"

SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "1eb1c330d281a9478d514724c5089225b3f66880f4a69e9e02b9d389f000adbb"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "e69c8c1b7b0df839b271a2c12985b1c56565a34f9b357631ed99fa9263f3d326"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "4d493bec7500dd3159c05ee8f2fef71241de575e1cb7a89bf128f01dc3e537cb"

SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "d5a154406bc6ebc6d5cdd3198aa2b5167ceef130454c56bcf03e37fa6b290882"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "8388ac7a5f924d5d6aa441ae97c33c1abf11fd9516f2b9853f7edc0c5d6c453b"
SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "de7ef61f6fa5f7ddddbddc0934beec58a3ce49cda96679dbf06895128f1fc228"

SRC_URI[cargo-snapshot-s390x.sha256sum] = "26353406aba935e135e0527673bfd3805de88ff9a63187135e73b5788f385112"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "7f909658496692a05dd241c15f79a02f80658d353013dace127fdc69dc5026b4"
SRC_URI[rustc-snapshot-s390x.sha256sum] = "fbfb02fff83f4a9d7298c511a3ad4b9e73c4d9d0be558edbf7226af0f586517a"

SRC_URI[cargo-snapshot-x86_64.sha256sum] = "b25d6f88b93cb75868ff4bc9ca0103facd4622825cf53df67546cea6cb60da0f"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "c8a3eaf26b83f1926d86b4db99ca16cbbff8e746e4c63f25f4d75a02a34a3b16"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "d33d493381dd17a4b491d0e978cdb6700badb5905e831dd5f7fe75ffbf8e0584"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "07fc71adf63691ae5e103c57c295e665b6dfc096fe4bedfeb5e1d33c133bb9f4"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "787f5cbbcec4e75b9beba3804ea05e36b7cb9b164c291cf7ce8f775d05634d61"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
