## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.98.0"

SRC_URI[rustc-snapshot-aarch64.sha256sum] = "00590657f2356d7163ca5ef295283523974c340fa21bb94b420ce794f29b358c"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "a36f7ac98af20ef0ba6368aace0345efabbebaef7962eba99f47190fd256162d"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "5784379d73ac881d15a9e67eed2882cd58c747c276221c23cdf9aff37e015ff6"
SRC_URI[clippy-snapshot-aarch64.sha256sum] = "bc9b5636170e4b067794bfb8962c6f22b2aee5cb7358f4d9523072bb2976062a"

SRC_URI[rustc-snapshot-i686.sha256sum] = "5244bd76687b64cd7305ef038d8f349774eee494174899474535c23dcbeaa009"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "11c7a5cfa324e4324611793692a863490902bdff8b9ccd197404711cb8dc24d4"
SRC_URI[cargo-snapshot-i686.sha256sum] = "b768fe07ae4bd233e5c3f864f935620abb8b72139f7369c869e964a0c30ae419"
SRC_URI[clippy-snapshot-i686.sha256sum] = "df4f34169dffa12cfc75fd246c75092734f2ff3837299531b0dad432d58f959a"

SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "5af789de2072c185302377c0bf6af5f6ce518670eaec62363142d60a90209986"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "397eb65ab17005c709a86e7bf7cb03d8aa9dae4303783a19c06ba8963247d537"
SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "e00ba627af9f098f0d9527c5b15f1a6886486c93f0139eee6f36cb104543a182"
SRC_URI[clippy-snapshot-loongarch64.sha256sum] = "7bda4fb4a971d23598bd488047fe8a4083f72c9af221a6018a84a8e304663c61"

SRC_URI[rustc-snapshot-powerpc.sha256sum] = "a4848482dbb50b808495a32d35ed5c7a7a9de86d5c9d7de1ca34a5590ce6056f"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "ae6a38ebf8ab0b8bd20da34aedc3de6f78d8314f10a6e373bd76a0dc90f22133"
SRC_URI[cargo-snapshot-powerpc.sha256sum] = "4e5760b7898b00c5b772fe7b1e48d75a0b4035b4a7d4de3ba6fc5b1544966196"
SRC_URI[clippy-snapshot-powerpc.sha256sum] = "a5c37e43b380adfeb14149ec0d9c4a76c985ec8d532ac669be9055094d05f166"

SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "c9833ed9d2abde4c6b8f4067408e9ff4a758e4252e87257d3acf780c31d3531d"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "e33d1517443f93dcbf151c46e3969bdbe4c9df4c847455915eecd6caee08cee2"
SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "fff7e0ca647951a4a42c2272c857fa749e28746853190e8211849da35d1fb45d"
SRC_URI[clippy-snapshot-powerpc64.sha256sum] = "02cc96e76c2ca4e22608fbf9080455211f0f6b51dc0ec7da0f54430181de7262"

SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "a940bca1bce862b2233a5cc07bdfb3f24b285744fc6ecb6dbb174853a41bff6a"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "ec370e4ad9712cde2f6664f18a022bd8de34c71b8517040b4d14144fcf1ef3e7"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "98b90455b3c57086b7ee6456da1075c929a495ac76850411db1e3cf76cf0cf48"
SRC_URI[clippy-snapshot-powerpc64le.sha256sum] = "2318acbbf599bd57084e4ccc75252723fefd7a9f7d4acd03ee1513b41c212b46"

SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "2f76a343190d8ffbe6c44666fdab32b6a6696a3e1176f6d84222b6e6221a382c"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "68ce716d38bcc15c6fe3686bc7133f5adbadd28f5f5a8a73ef1172b4f60149fc"
SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "9fcbf7ebb55a3eefdc2145a29b1cc797380edee0cee1163742c5ee06c4622347"
SRC_URI[clippy-snapshot-riscv64gc.sha256sum] = "12b08f51925b9eafae1b3ddccbab49024a05827fa965e65dbccdd63422f84b1e"

SRC_URI[rustc-snapshot-s390x.sha256sum] = "c6e39df3937b6e2c2c66eb67406738036ed80f79072d136e122e61747b139b85"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "c9bcf3eb8348e04502a49345a0847576e6c6fbd6600d566ef7cc74f6c2d06048"
SRC_URI[cargo-snapshot-s390x.sha256sum] = "0bc2062d88993fc590007ea2374f5e784fb5b7f1eec6c024b1fb3459ccd50212"
SRC_URI[clippy-snapshot-s390x.sha256sum] = "25aad07f775b7eaac2139f1f233855f8fd81e40ea8450e2220ce9906bab5f123"

SRC_URI[rustc-snapshot-x86_64.sha256sum] = "0e37cb339f447fc44d6d781073bacacebfdc5612f2600e4c7e84c266f5f3aced"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "f5022e6c95a5ad23cca2513dc8281200f585fa188de6370aa37b128a43f876a3"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "2f512d170d3dd23e16ababcda32ee2e6d5172d861a7af1f504e0b1e270cafab9"
SRC_URI[clippy-snapshot-x86_64.sha256sum] = "646c6bd2450ea4c32a3c78ad7a3727294eea1f1519718a3f1487424021695ffa"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "7d150f9ec40eaaeb4ae453dedf5416855b9245420fd89203a5da420f692ead38"

SRC_URI[rust-std-snapshot-riscv64a23.sha256sum] = "81fec8bb25e1377e8f143c33441d12ac115781da496475ecca5601cc791be56b"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "9e0a10697c2e68707573fbcc01aa927d32b9652deb771ca87d69a9b7225580ed"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
