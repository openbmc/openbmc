## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.66.1"

SRC_URI[cargo-snapshot-aarch64.sha256sum] = "61736a6ec61a4eaa9a7b3f219d9f8166ae97c1acd54b5e904c15fb8caff06250"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "9d96ba6a5a255339cd04af079d906753d56c85503c727279c227d24bd4987e24"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "b4d842594ba11a84712c2d1bbf7f7945cfcc2485c72861a23b488fefba5c6a45"

SRC_URI[cargo-snapshot-i686.sha256sum] = "fb0c1aa922c4e41fead9d35090769309cb45648d6f77ccbe8d2ba125a75cc2af"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "b32cbbb9002f8a20b302823a2ff122a91f077b5e1a7bc1340de690ded86bf10b"
SRC_URI[rustc-snapshot-i686.sha256sum] = "e2ab464f43d6319ddb9796cbfacd444dc40f3d0e7503d672c23ecbe0daf11e1c"

SRC_URI[cargo-snapshot-mips.sha256sum] = "0f03d6d35b6044dc81a4d5eb9bd42c9d1e8c0e85363f960ff921ebe46294ae45"
SRC_URI[rust-std-snapshot-mips.sha256sum] = "e6029b4cec5d6c06c34a0dbec614fa252fdc8cf31013d6e561112cf90f742568"
SRC_URI[rustc-snapshot-mips.sha256sum] = "64ad92525ae1f018e08c055d7892e0fc613437bde8cf614b1c2a8986488290e7"

SRC_URI[cargo-snapshot-mipsel.sha256sum] = "74d757a456d2fbb418c253db203c0bb3f71d797e4ab3e2804b6c594a18e0f199"
SRC_URI[rust-std-snapshot-mipsel.sha256sum] = "8bfa3d6079c6df049978fe61b1ccaf992aecd006e23df2439ecfc883bd8bc31b"
SRC_URI[rustc-snapshot-mipsel.sha256sum] = "f2674d60ce52c49048e9823af57aae24bb6722e8998783819ec884222caeccf3"

SRC_URI[cargo-snapshot-powerpc.sha256sum] = "f771fa4294c8e0d5d0e58129fe9d4e0913566dd43523b6f0af19a08004004df8"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "417b12a9bf090d694514937cd8c321ed625f155248f63c0de8207b17fa4b35b1"
SRC_URI[rustc-snapshot-powerpc.sha256sum] = "ff16d02c100086175b9fbcfff4d3705fb4f5b58a6506ec7667dc86c56b8bb3c7"

SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "9a933d39e6b028e73db9cc0959af84128824e0b11554e3a0171cad7635a343c7"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "7074c4ea9fcc683ceb0adcca6ba07544a1d91f0e5d2a4cf14c81eb14316ebf35"
SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "f5bff79d517e2f721839462881331bdc1b8323a434f4ebe0529f93213adb2a24"

SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "29fa2e003f4bf3e37a3fb506d5b7ab19eb6412b5966d865e082b354637d5d84a"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "4c64a09be872b5832b50d681fbe29691b6a5d3e23ee5535020fa22b8b453c770"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "3b4322b519b0f7fbcf88511b2061be1499921517d810d7696be58a16467d4589"

SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "f362cf575fde92f988d5b1fe19fd463a1905d8d2a3844168034df49b1dffb10b"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "776eff6f451845e88224714ee6da7819e34f01b7625bf927394c2b91e5c8243c"
SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "84bea70d3acb6af04ae4c0f49f904bba4e2644b92c5996aacbafd7610dd0e147"

SRC_URI[cargo-snapshot-s390x.sha256sum] = "f69ea091fa1ee4871a46cb9fa1da5b81fa2980687f3bedbc4677a4a82f8da0c0"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "51d1567e1d28e43c2165886f5a4955dcdaa41aa5ddcec5fb08200491fd1f6062"
SRC_URI[rustc-snapshot-s390x.sha256sum] = "25a047db8ec0627bb7054eafe6edca6ce4c473b30d6766b30cbff1c536d0673b"

SRC_URI[cargo-snapshot-x86_64.sha256sum] = "a636f83eb2327a66f484b9592ab305c6642df16fc80d0d1cb727e766a60da904"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "c5e2c9b160bd8d99514f13cfbc0e42a722fd9ca14e6aaca4b9b77731a7a48377"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "242855e2626860aede6957dc56481cc02acf8cad12fa5bbbcbd93f9c51f0b3ad"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "90eb8e2490283930e682b79842d664d4867414563353d53dafc47eccf44aea17"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "3aee03fa61c28bb242023efb09a22e38a76b075cc72bcad9894560be8b28a927"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
