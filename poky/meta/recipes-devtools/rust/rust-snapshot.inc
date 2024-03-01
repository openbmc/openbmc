## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.74.0"

SRC_URI[cargo-snapshot-aarch64.sha256sum] = "a18dc9132cf76ccba90bcbb53b56a4d37ebfb34845f61e79f7b5d4710a269647"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "c5ad01692bc08ce6f4db2ac815be63498b45013380c71f22b3d33bf3be767270"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "a49bb365481913ead305658e7e9dc621da7895036b840fb57b1bc85c721d07e6"

SRC_URI[cargo-snapshot-i686.sha256sum] = "9f5b5226a69f95950a381ec5bb15dde7a90865a6df8aa0b470082a40d42d9f38"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "69757b72def9c433753e8bb575c817fc1ba389cf1a9c25276db1491ec025e495"
SRC_URI[rustc-snapshot-i686.sha256sum] = "7a2bc1bf7e51942d32e82f461eacebe7f929c3eec210dcb7dc6624efd997d7da"

SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "77d6d55122150d8fc56d31fb166fd1b2ae48bff7376459c1b0030727fc604998"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "13b85a882e912d0d8b3228feb5c263d34ec353d483c9defbd3e6bba38935553b"
SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "703e8c81f9ca3100fc459db92fd5899de62cf77393f334f98159cd97feb11633"

SRC_URI[cargo-snapshot-powerpc.sha256sum] = "08ea8a345839f34d26f21b94ed6d458e6a38513999f7ddc05175c371983e6deb"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "458ee056fbeccf1cf96c20506654e5e9104c4e8f23d46cd4bb9b97ff5b3f4d55"
SRC_URI[rustc-snapshot-powerpc.sha256sum] = "d4095cbe26ec197274dae9409e68843653e8c08c0b79e8cd74e72d9907e99816"

SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "696863642318f139634e6856f5e946ea970318ce79d4d9b1595871a70a662a89"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "7ec56629b7d887753ce3a895fb73b77d2d395acac30207c2b69237ef63279872"
SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "ca162463db262df9d646687386a1c19f15c8ca9bf1f29eea94f2a8a6d7a6102d"

SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "2eccd404aabe5137a8e45b6173c27d08862a0e674d5866be71aff1434f271d50"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "785956d68855de18546c87d6d06cd2505cb8a10edba84327bf2b448420a31d55"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "8727b1a92e88ac1ce05198ee185dac86553edd7f50b726781c9ab64544b59809"

SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "5b224e465e006b5fe959ad64d0df0540c4318ba4e39edd89794d520eef60b026"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "2a500156825dde03a53c965e5764a440b1ebce973b8a31f21e8bd8104271d56e"
SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "f4f27f1c40208b61ea7e61f9edf2de1787aea78a1edb7fe15bceb20de5c7a4a3"

SRC_URI[cargo-snapshot-s390x.sha256sum] = "06267377c811271d6e4ba6feea1d4b84a9f4c5c8d1dbd46092d0a0595f24e9b6"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "35142541b88a1244c8225c64ee18585446d7e67841a9335ccaa95acf2d34dde5"
SRC_URI[rustc-snapshot-s390x.sha256sum] = "41eae7788549aec58a6980ae6222d3330a01a37d1e7856d087a4e9c8a19aa890"

SRC_URI[cargo-snapshot-x86_64.sha256sum] = "f219386d4569c40b660518e99267afff428c13bf980bda7a614c8d4038d013f6"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "548413213012e2f62b08ed8a913a51210ae7402619027224580176031f2789ea"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "7d464be2ae0d6ce69f056d1ea9a8ce2b3b1d537418caea216fdd303903972181"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "bd4502462c5e2b2617b23f28862e544f14c4d02658f6d331f0cfbbba914aa4c0"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "68e96875ca7fc6ed0e023fcf752f28b95e9cc7d9881af4e8e167259fdaec7168"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"
RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
