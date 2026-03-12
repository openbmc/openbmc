## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.93.0"

SRC_URI[rustc-snapshot-aarch64.sha256sum] = "1a9045695892ec08d8e9751bf7cf7db71fe27a6202dd12ce13aca48d0602dbde"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "84e82ff52c39c64dfd0e1c2d58fd3d5309d1d2502378131544c0d486b44af20a"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "5998940b8b97286bb67facb1a85535eeb3d4d7a61e36a85e386e5c0c5cfe5266"
SRC_URI[clippy-snapshot-aarch64.sha256sum] = "872ae6d68d625946d281b91d928332e6b74f6ab269b6af842338df4338805a60"

SRC_URI[rustc-snapshot-i686.sha256sum] = "72038681a14fe632e2b550392b83dadf40325c472dabecfff40a9efdc2d8e201"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "b8b7020a61418b95c1ea26badaf8db6979778e28dbadddc81fb5010fe27c935b"
SRC_URI[cargo-snapshot-i686.sha256sum] = "2c6fb5893b4abcdf6af9dc3c4f60abc072318b0cb13f2bd969585910b7ba048b"
SRC_URI[clippy-snapshot-i686.sha256sum] = "e8f6b16356548e2dbad3d380c07cd7b3654b49c513a15a2ceb9d17a49ac0aa04"

SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "d5762adb6e1dec7e6af4a7347cde45c12fec3f0ceba8a05aff6ef00390105739"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "4c5e2e8fcc43aced123283e3c8c99771c99750b5c7ecdf95a45258f5e26618b2"
SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "cf1af58dad1098c1f56d3df11f884b8d87b0db30ef038d1527872b7cdd7c9785"
SRC_URI[clippy-snapshot-loongarch64.sha256sum] = "41f588aefa9e8bcdb4969afc14d18b19b4bd524defc026aca8c0275c19d183ac"

SRC_URI[rustc-snapshot-powerpc.sha256sum] = "b31f5408c198756b57f0491961cf6dc84bfd96ff1c73627798015f27ecea6ef5"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "e851c0fa3e726ce3f7139c5803198a1aa9723594394734ac9e373c93d92e5ea3"
SRC_URI[cargo-snapshot-powerpc.sha256sum] = "f8472c773385820a9c8fe375f766b6918bb659eb2d517db72eff86a44c59546d"
SRC_URI[clippy-snapshot-powerpc.sha256sum] = "9f48807b761998db304ade11573742f52dcd61bf62f3e339eb9975ec33feab65"

SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "b483c5324966b7138fb3f6180f7e4a2e9a5162179a83e58bb52d1ee5036009ee"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "f729bb7d95705e12a92eb072e494b93d8822ca40aa4802ca780b0cf33b56d401"
SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "662a9d454073c178c052bbb001f7440ded82134185c7c656ba8fc37468799383"
SRC_URI[clippy-snapshot-powerpc64.sha256sum] = "2242d23c8472b4d7efe6e2e538157f0b25d9ac99f6362b2cd534ba5030b2a71f"

SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "fa0705079ee9eeaeee2e4f12a3ffb35ccc3d4334cccd3eb3106a3f59b96e88aa"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "d209ac698a69ca9b9035adb97a0ed8e60a08db52960198c3e03b9ee714c1a46b"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "a587e9dab9385ea0651dd45a6cb6101d6028ccf578a3e1140ce083fe9419921a"
SRC_URI[clippy-snapshot-powerpc64le.sha256sum] = "283b76a665927a7737dbf422d3a3459eb85e873a49f6a2de8cc0ab5a5c3a8350"

SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "804b19ccf61e8aec4c0c6d3f115961982b155d96925eb34b651aa7f6493a2e8b"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "b769fb6c9f3e0419a6bd0b7b79f9191bbd7a48a9f243b23eb7d135711aa6de1b"
SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "1f0fc840db26c426b26ce8a943b887756e9552974a39248ad0166960fa169f62"
SRC_URI[clippy-snapshot-riscv64gc.sha256sum] = "1c8bbbd36200f525949a2a3b1446ddc863b99bdc2552b818919fd64cc27b64d2"

SRC_URI[rustc-snapshot-s390x.sha256sum] = "756c25d32d6539a7485a278216f3058c1571fd503c638e1179b74a68611dd1a4"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "41a65db45a288eb3eedb187b366f132d5b3615767de7ce994b123b342ac7a848"
SRC_URI[cargo-snapshot-s390x.sha256sum] = "7d0281d26142bf124c0393a99e73b7b8ae6cb579cb7ebe50f696da6922d2129a"
SRC_URI[clippy-snapshot-s390x.sha256sum] = "1cf85452916d4f5696d93e45849a99aba23884a3bd3d886d0179a1b2cf7d2858"

SRC_URI[rustc-snapshot-x86_64.sha256sum] = "00c6e6740ea6a795e33568cd7514855d58408a1180cd820284a7bbf7c46af715"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "a849a418d0f27e69573e41763c395e924a0b98c16fcdc55599c1c79c27c1c777"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "c23de3ae709ff33eed5e4ae59d1f9bcd75fa4dbaa9fb92f7b06bfb534b8db880"
SRC_URI[clippy-snapshot-x86_64.sha256sum] = "793108977514b15c0f45ade28ae35c58b05370cb0f22e89bd98fdfa61eabf55d"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "03534ebbd698f7580cfb08798a7451e86bd9ec46327f4b41ccf2c2f8f1998438"

SRC_URI[rust-std-snapshot-riscv64a23.sha256sum] = "2a625e37c5b943c6c63dad7f52187572d1a65854e3950a8fa26e593f8231d711"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "360f0a4eb41c3cd3792dbe756c0c5b5a36a5b41e3771b8a12d5bbc97bf51fdd3"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
