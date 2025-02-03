## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.80.1"

SRC_URI[cargo-snapshot-aarch64.sha256sum] = "a8c4f1ab2f65e7579eb80153fd1ca9a0b365ca31ca6ae0ebd34156e0724dfc60"
SRC_URI[clippy-snapshot-aarch64.sha256sum] = "3d522172f9797e65c609a640af7f4ac331525150c91f93e41798c5578e9523e9"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "8fc4bfc3a5fe64f8530964a5ea3bda95e39357eff14d6a8bb24f010ecc912923"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "fc21ca734504c3d0ccaf361f05cb491142c365ce8a326f942206b0199c49bbb4"

SRC_URI[cargo-snapshot-i686.sha256sum] = "b0f6d95c8a292b1276f30b1bca14197b28297c2fcd51974ef320d05497be3ca3"
SRC_URI[clippy-snapshot-i686.sha256sum] = "a6899744ed734dd3a9f59631a943721bfe67ed6273e60dd67e8636820c1bfffd"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "6547876dcf243aeeeadaa38dbdce02c316b6f83305a5337520e02eaffc90834b"
SRC_URI[rustc-snapshot-i686.sha256sum] = "b40c1437491d0a24756b6baabb14c2d4d0def199bc652f16e417addb171ac977"

SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "843272f7ab9b2f8c24f1747249275e0472fb2ede90f3a404e897559ddb898c18"
SRC_URI[clippy-snapshot-loongarch64.sha256sum] = "e4dba3b66bd8f811c8508fd3be1bf136b714018768d9f16a96f601ebd375bcc6"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "68f30f3743b573e205ecc1733e1250ffb8d8d81e131c3b2f4a819b7e1e610d62"
SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "5ae96e69573690aa48ffb9fecc62826ffb5d2fc280d19d1e54ab36ff48e28b68"

SRC_URI[cargo-snapshot-powerpc.sha256sum] = "a88402d95f2959caaff20037144d3109a69ce3310e1dfbb27231dacd50fa2988"
SRC_URI[clippy-snapshot-powerpc.sha256sum] = "ae1762dc8fd679d65154d5af9500c7539f55ec9d049ab736a910893d382fd6c0"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "88e8144c25ef8347471dd53eea7af62e5b31eadf0788f4a82be7560f5a0be255"
SRC_URI[rustc-snapshot-powerpc.sha256sum] = "5f435b48316a719c87fb27f49c0b37884cef7dd3ecba76df9db2a4008cc03458"

SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "be2707eaa24f34775c98044f92c391a2c6037a2214adab9e83c62903001fb577"
SRC_URI[clippy-snapshot-powerpc64.sha256sum] = "a422a3b638c42f26ada441d2940a44a8c0f1ade9459f86d48a3d8fcc866bc60b"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "cb5ad871a0a7efe7c270cbf319d006a84e8f8526acb22ce941f2840fd0e368c2"
SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "e3257dc0790728498cf2773c3ca66d728381cc6d1f403969bd282223bd97aa3d"

SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "a523cf21d751be597a19830136e3479b7f22d3642d95f53f78a11e770d971d28"
SRC_URI[clippy-snapshot-powerpc64le.sha256sum] = "93ce75f4edf2c3b792714d33e279c5743d9d3ef841f8f2d8a7e76c9c7d74f952"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "06633b26384cba594424ec02acf0d64d4724033a856a2e039a18a518cacf1d3e"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "5f72f8723a2023b0bbd64cfc3d10959c4210b68579ad8e174809d17e1e2ead36"

SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "29e996af02293562f6ee79032a5414fffbf77e75cb7f0ba89053849986cb6303"
SRC_URI[clippy-snapshot-riscv64gc.sha256sum] = "24465ce5fe8f04d9b54c31a2c5bcaba6d702925ac3cdec0af5b954643013db90"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "1fbbe8df7596682466ae2fca534d5f6bae8b3f32577450e2632955268a786a06"
SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "838d78ef8b9a11751b1dfb2cf2abfdc845deca8f0002c11930d54577b433cb93"

SRC_URI[cargo-snapshot-s390x.sha256sum] = "0c22278b4e8afa79de0cae798575c9ef258e73d0555b18a84efd5d042c69c6e2"
SRC_URI[clippy-snapshot-s390x.sha256sum] = "52c3368de0e01b07f89f74a4b36279be1b7d2312b05253c7c9ccb50bc38f263a"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "e732ce690fd63e3c2576bfe83a556031c4805c17b35b8a1c1a0ddde5cee31d46"
SRC_URI[rustc-snapshot-s390x.sha256sum] = "0c06439db686645be36390969b503996608f25954eab2b7fd9a2915da6c0bd7b"

SRC_URI[cargo-snapshot-x86_64.sha256sum] = "da9340b3249f08656cd4fe10e47aa292c7cd79903870a5863064731a00b5b27e"
SRC_URI[clippy-snapshot-x86_64.sha256sum] = "e01d434e952821900f37824c797f87ed16db79e54fcbd2f396b2f1b5cb2e3c55"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "b793405538d8b6ec1632779efa8835b07b8987db2008c5c9c809bc4b17dcb121"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "0367f069b49560af5c61810530d4721ad13eecfcb48952e67a2c32be903d5043"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "94927cdfa7cad391700b1a77730bb17aa364831ff6a6c40ce6500a14c1314647"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "ee7fa0104b019eec22750ac635e699d21dbb5430fc6982c495533900ec568d5b"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"
RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
