## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.83.0"

SRC_URI[cargo-snapshot-aarch64.sha256sum] = "5b96aba48790acfacea60a6643a4f30d7edc13e9189ad36b41bbacdad13d49e1"
SRC_URI[clippy-snapshot-aarch64.sha256sum] = "08cabe06aa116b78b351ab1ca1d7ccf0dc18d97d999f3e0bf228021291314d5d"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "8804f673809c5c3db11ba354b5cf9724aed68884771fa32af4b3472127a76028"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "aa5d075f9903682e5171f359948717d32911bed8c39e0395042e625652062ea9"

SRC_URI[cargo-snapshot-i686.sha256sum] = "906976544e7f6bac7ee4f54eae56f3162fe7dc06744cf84149699be57bd0b4c7"
SRC_URI[clippy-snapshot-i686.sha256sum] = "4fb7ed75796336f4972675225ac4e1f2a8ba58ddf350dd51f1d482c7084b0c6f"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "d6763f3ff18fa6da4455058fcb84ef19c2586fca02badab466200d90fa4184d4"
SRC_URI[rustc-snapshot-i686.sha256sum] = "421fba974b062b1b9749792466214e563589a05f888673989b0208b1dc66ac43"

SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "c7b537bc2bb4b685eae80503657b89bf59c51303c90ebc042b0fb39b21cf252d"
SRC_URI[clippy-snapshot-loongarch64.sha256sum] = "895d1f9cacce934f0e72e06df9f4653c48d2df4a2070206a6b8b21908ce07d1f"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "5804d1e989c03620b3bedf0ce43c98395fcfae1d6923446c31ae5562ee56916e"
SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "b3afba9debf0e153f5dc69ecb0b020c0d6159002bd3b4b845c6797480c07d50e"

SRC_URI[cargo-snapshot-powerpc.sha256sum] = "05c353dfeaf51e960e5f916e345f0f0d0497851428a3faa997a62a8563e63109"
SRC_URI[clippy-snapshot-powerpc.sha256sum] = "0e93e1bac1c195d89b9b94a900ffa3191228c6c66f5a5bf2f656a1a72fdfa163"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "17978a56a8efef883485201dc701ed2ff13530511578da240840f12bdc55cb27"
SRC_URI[rustc-snapshot-powerpc.sha256sum] = "f843dadf7f1f0148e78480dfcdecaa8eef15aa8382576ff41c82799c6233039b"

SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "889f2cc33c77a34ae962629f0469c6138a5d4d6efeb5f383a87443181845a771"
SRC_URI[clippy-snapshot-powerpc64.sha256sum] = "35be1a0ef9241785210447126a18fccb4114fe54776f3e20e21d89c025b05498"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "adcefafca7a7b7ddb220d18e31afdfd4c547f87b1515e45098513b2d468043e7"
SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "267089f5c1d7364ccf56f2246f37d668d386edcf2154c985a6e51c928e50dde6"

SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "7a1b7b1d18f562be446db8880fbcc26f4f5581c273a5d547231e8da3d06e3f39"
SRC_URI[clippy-snapshot-powerpc64le.sha256sum] = "7f6a3b6c99a38a0a6722652c8b342b16397000f198ff5b8ae5c042336a88db89"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "d39fdf0954e57fa9dd0763d58e7897bbb93265ab217e5fef14a11fafe2f706a3"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "5978f9b3246c71cefb5266e56403c1071fed70d71019bd839c4601d7b6f9eac9"

SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "ddfcc41a419f1d1a7637a972932d37e3c5487343f9be3deaeacf06027fbb0567"
SRC_URI[clippy-snapshot-riscv64gc.sha256sum] = "feea5dae4dfb65b608590b5e7b5c693da12f8e559e3b1a7b6b19dbae580b0ede"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "6a5467d1f4b8040f19c94ca332063cad57581d6cfd1f15543da5a67d4d244f70"
SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "3f0c56d778985f69aff3ae58aa157025ad807d8b5821b8f98108be6f8a283e21"

SRC_URI[cargo-snapshot-s390x.sha256sum] = "2044ee69985da536dc1786e949828eba88516ee01ad88a4c36d753c471143ccf"
SRC_URI[clippy-snapshot-s390x.sha256sum] = "fdab081200826ba47929030cd7e9d82c843122fdd26fc3edff9e52a2d91f68b0"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "d1e1af8622d59c3351046ded96af4eeecb3db2f4f7527249eaf1acc917e3f2e8"
SRC_URI[rustc-snapshot-s390x.sha256sum] = "ccf73e94f3f60dfddc26640df95dbb82939aa572d50cda0aeafaa36b47b28f07"

SRC_URI[cargo-snapshot-x86_64.sha256sum] = "de834a4062d9cd200f8e0cdca894c0b98afe26f1396d80765df828880a39b98c"
SRC_URI[clippy-snapshot-x86_64.sha256sum] = "ef6c05abcfd861ff0bca41d408e126dda195dc966ee35abee57645a12d418f5b"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "c88fe6cb22f9d2721f26430b6bdd291e562da759e8629e2b4c7eb2c7cad705f2"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "6ec40e0405c8cbed3b786a97d374c144b012fc831b7c22b535f8ecb524f495ad"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "7948ff50f7e83a20deba9d4f55170f7e3b0d805e80049f38b19f41580a9d7bb1"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "e8684ba5d337dbb0594cdc8e4e8ffa11d7ac8e48b2f294019e042e341fa9a869"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"
RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
