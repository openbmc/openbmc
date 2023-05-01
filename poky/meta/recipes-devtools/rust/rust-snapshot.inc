## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.67.1"

SRC_URI[cargo-snapshot-aarch64.sha256sum] = "b04e33d9beb3cd97501399c17ca297f0adc12a7c0ea16351b8821fc9228de477"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "35027409400faaaa38ea2855b92dea6b553b2e656dff735da730f67ac02e57e6"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "053ccc4ab81ea9c16906205c2b653a93b5b44fe4f67f9e45153a2687b7efb65a"

SRC_URI[cargo-snapshot-i686.sha256sum] = "36db46e19e8573ea113c38d88f66def5fad848721b3c6dd69361f197b7a02f80"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "aab2d7aa76793e78c9c8810e93ed8978f6422843b1277e9c60337b0f943a4409"
SRC_URI[rustc-snapshot-i686.sha256sum] = "0c77fde6daa80825f8cb81a5525c99db238a3ab4f0b226470964062e74603dd6"

SRC_URI[cargo-snapshot-mips.sha256sum] = "06778d8aedd8e7850f0175483efa015cc2eeb69e6027e3d94352d89d2a510fc5"
SRC_URI[rust-std-snapshot-mips.sha256sum] = "048b3da190212b742f7a052618b57683dc96ee2b6c25fc7f5ae751143cb79bad"
SRC_URI[rustc-snapshot-mips.sha256sum] = "d3b7501acdfda6e4e9d8ee64d9de716579791d567c66f1273e601a55c2e23ca8"

SRC_URI[cargo-snapshot-mipsel.sha256sum] = "7bf800ddd0ee6b69bb66fb7cdf12ae045db025fdb866e00c62b63a1eb99f7d64"
SRC_URI[rust-std-snapshot-mipsel.sha256sum] = "94410897626546dde806a114299c3c32abb6b4b294af5daea5c4d136751fe063"
SRC_URI[rustc-snapshot-mipsel.sha256sum] = "eb34b5d901807e1e47fb6ac686571f21b2a80ad831583485872c9e9c59291776"

SRC_URI[cargo-snapshot-powerpc.sha256sum] = "b134c63817805f59821024d377196f2821ae53f88d0a4a2ebdbd8152433984b0"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "26efe78c09ed36da34964c0d023cf9aa404766ab790a94d9becd373b8879cb5a"
SRC_URI[rustc-snapshot-powerpc.sha256sum] = "8f6212756d152f6876a6456f4d3f93ef8b4b7e5c93b96fe515cb8183c0612d7b"

SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "2cd84deb15ed16fc4a1d8a9de45577ba4d9f245de4f7efb3dc6611eec437eef3"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "135f5e6ab6d8f077a14521d387d3f31831abdb840ca4e6a15b6f0d0d5b7dbc81"
SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "bdb08b549df5d7a421637efb0be00211b5ca9cf1e4214d66eb762b92e0f9b3fe"

SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "fbad853a30437653f09001d0de797823dfa32d537163841bec1792c3267f5eab"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "364c47ed22e4268edf238d221c40007d7d8792a66ca325d3937596a9c911ea48"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "4a2368996acd1741200d720674101323fa951dfa580b64c8b2e9fdc09bb22534"

SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "3381af9ba39068b9c5e62536125a3abb582aef15932f63cd4f90df0cccb05ac7"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "63a7e6a6c889a3d01bb3c66a3def3e41963511ea52e13906669da5b29b2fd9da"
SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "916d204d727f485abbadeef914ee881cf61fa3c167741a9ed29c9f4042c99e25"

SRC_URI[cargo-snapshot-s390x.sha256sum] = "78d7c2714015ecb7283b417cb265d4d604379d0720aab5f611ca1c113987c283"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "04573298d9e815c1e8c47a2f9548ea55d4a2afc538eceaa6a704d44a5e1f7e3e"
SRC_URI[rustc-snapshot-s390x.sha256sum] = "e786d34e10069dc00774fe53bca092e537e96db6c9d5258a8b0221e4fa5a2caf"

SRC_URI[cargo-snapshot-x86_64.sha256sum] = "e744dad75de8419e7fea530c29bd56cf931b4d4de62eb4bd442bfac7b54e61ed"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "f4dc8468dfc1dbd86f865b10f06e0e4b4e76f5a3a1cc27317a520ab1660844e9"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "e27ec0c6d1a2b2b38e5258904c3741ddb246bff5715aa95e595f818aa77f7bee"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "22cbbf0b22d9b0894cfbd6e1dd6b95b71c7ad02cee4e477d375dcf81af8d1337"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "addf0b0beb1966787e599f32849f18c28d5cb65515272b7d9608d4a1dddee2e7"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
