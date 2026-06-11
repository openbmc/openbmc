## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.95.0"

SRC_URI[rustc-snapshot-aarch64.sha256sum] = "0fe3689eeaed603e5ef24572d11597d3edadaefd2cb181674ad621260f2501d2"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "3a21b271b1ff973b94d69b25e7a39992f9fbcae1ab6d9475844a23e6ad3908ac"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "7c070aeba9bbf12073646995a03f36c346bb5f541d0078ba6d9dc2a7adaaf6af"
SRC_URI[clippy-snapshot-aarch64.sha256sum] = "fb021e0c0fc2238be9266d7614f4a26bc372544c4cba3528d729ab24ad229fc9"

SRC_URI[rustc-snapshot-i686.sha256sum] = "420f8fe21a8f668112d452b9d9533591c7dbb534fb13bcde9d5c8e56131b4456"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "527c5d5249a7f77b48d3c9da3ac512d27b47f43d08dbe3c6f82a3d5b35d8aa27"
SRC_URI[cargo-snapshot-i686.sha256sum] = "5292abf16427edd5e759f15177a3275ad21826e7a19fec58bf2ed6fd0b301f20"
SRC_URI[clippy-snapshot-i686.sha256sum] = "8a6484dd4dee723a043006b9bdfc7b8440f83cff8662321ff28fa941d6f45b1e"

SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "b1d248bcccbaf5a53a335c8882022af4f40879ac11ea85c1bac0500ed010fe33"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "eaf2c37c3293eea742e7ab20f25718ab19c93bd381df8823113fce70460c19c3"
SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "7203c690167b73dbe4c7cce03d302b437c1c422b6c3258a7c46d7fc1515820f8"
SRC_URI[clippy-snapshot-loongarch64.sha256sum] = "fa0e8be99ddf2aca72aa7a911ecf43da80243a9a400d8c9d9c9c3ea994848bdb"

SRC_URI[rustc-snapshot-powerpc.sha256sum] = "a0c42a1dc1aa7e06db4f17cdcce6a150c803e99118923ccddf88d66677acf48e"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "59e0abbaa246502521e37c55b8d6cf88d5b8a697b0c70c61ec189937308f7246"
SRC_URI[cargo-snapshot-powerpc.sha256sum] = "71ef5259df76102d0380c2a912384ac61ea280211fc4e2087744c476f2c8694b"
SRC_URI[clippy-snapshot-powerpc.sha256sum] = "b12ffc360997ed6ee4b93c1e3bfccea84ab034d657f4411bd34ae4494b685c51"

SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "946a84e6e9d6e13df5d6361578d9202714c08c2ebd3e09734df7cf4afaec60fd"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "cc7fb9aa289ff1756502ae16a05e2885289165f01ed94a7c2db6576b3dae74a6"
SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "e9909e9dd0c33e833dbd9c6b677aca49eb4ed2c09a30f1577c91aae70690674f"
SRC_URI[clippy-snapshot-powerpc64.sha256sum] = "4e7c7d14165c2aef1275aac109092c2cd85396a483756b8084a5ab729b4580e0"

SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "5333b31e79482c71337cae23074483cb392e708528e383d587a9cd160bfb63fa"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "2370d9266051a0b23346d42e43a00f91b2daff22a963fb03e28ae50cb0b76c50"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "a7744cab4bf40d78ff8344d320bb7a41fee4af1f2fbb6eb67aebcc13b30a49e0"
SRC_URI[clippy-snapshot-powerpc64le.sha256sum] = "2d29ef33a42d2f2291fa0107e48ffc35b7be0d318ad6bfe231fb1b751a0fe214"

SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "04752f3ad26bf07ddbe70b1dc759aafde75226dabd4fc6cff3d0b016e293b990"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "50fe7869e166bb4c990a0e1664366b1ffdbe669664b7663cd03c079bd0efdcac"
SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "240d7ff48881232b78ea31648621832b36a401872499d0a3a1419e4a9da5c43b"
SRC_URI[clippy-snapshot-riscv64gc.sha256sum] = "e72e423ab27da0f0f38b71ff622b357028206d551d10b62cabf6845111805701"

SRC_URI[rustc-snapshot-s390x.sha256sum] = "4f3812e0371d8eea904b0fbb08b94043d2ba7227632239e94ce7b35ef5285c2e"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "31978c1286afff9a0bb7f01c2ae4a39f40727b6100a82b6d934f146b06cde510"
SRC_URI[cargo-snapshot-s390x.sha256sum] = "01f2854ed29843949fb5baab852316c36382e9df6d39166508006c1dad830f08"
SRC_URI[clippy-snapshot-s390x.sha256sum] = "39ca2e54ba9945ca94ffa4ab9a276f192d993be331ed25a53ab0e1039dd60528"

SRC_URI[rustc-snapshot-x86_64.sha256sum] = "8426a3d170a5879f5682f5fbdd024a1779b3951e7baba685af2d6dc32a6dfc15"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "047ea7098803d3500fa1072e9cee5392697e21525559e4458128a2bf874aa382"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "e74edd2cf7d0f1f1383b4f00eb90c843750bc489e2ccf7214e6476678a907425"
SRC_URI[clippy-snapshot-x86_64.sha256sum] = "ac779bc9839dd47180806b133e4e2563c4a34716284cd5b8fede8ef289f452ca"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "dc5187f4062d617561e0d7885ecd4d4d3f995435b38d8c53d6451b56808946b8"

SRC_URI[rust-std-snapshot-riscv64a23.sha256sum] = "87f3cee68f0522e0c2b755d3f9683473a3f51064e58d9b02f21350d5fa7af9cf"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "88619b2413d218c119a2060e583a9e835fa5f9cf6ac038070eec10b02c191056"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
