## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.86.0"

SRC_URI[rustc-snapshot-aarch64.sha256sum] = "ccece9e59546d2e6ff3fc3b8f4b033aab21631c271eefbe814b3cbace6628c6e"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "176129577a5d560bbd94bcd2d24c0228bb495b73219df02556b4e4b4f0815bf7"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "37156542b702e8b4ffd1c5c75017632582343e93ca378285cdc92196c85c77e3"
SRC_URI[clippy-snapshot-aarch64.sha256sum] = "e70edcf560e6b50861618d7045d2b38cae9e2a555f489062570e2e7041563f42"

SRC_URI[rustc-snapshot-i686.sha256sum] = "976f4604d949d722738a9fe95313035226571128396e942b0fc678180be487b0"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "ce9f0fa4b0760730e26bf315cebe099f0b883436e095c5fc4b94ba20bd9f121a"
SRC_URI[cargo-snapshot-i686.sha256sum] = "24f20bdc1bb14f4ffbdc2f540488bebc3340437418725c3162215ae03cdad480"
SRC_URI[clippy-snapshot-i686.sha256sum] = "6788449fa0eceebe4ea8c797bbaf27495dad152087b3085d58dc05cacab0617a"

SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "970089ad61f8ca82017b59444aee483c1fc005e3f7a6af63cd5f146df8287cce"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "2f528377f57fbf81da35e2f08ec7ba50daddabebdce2cc86b6ec909fee157a33"
SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "c3b8ab03b64c824f2ea25db578b5760d44302be3fd1e4a78404c98cba39301f4"
SRC_URI[clippy-snapshot-loongarch64.sha256sum] = "1ab711423497a1d02408a240da28ad366450574dd7f528ffaee80d1d5ed10550"

SRC_URI[rustc-snapshot-powerpc.sha256sum] = "21ea61181ba7daa04f61bec6a295ad76ce34bfbd74ad1d679c4548977bd82eb9"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "e5ed58a861619bcc89fc3f969174c84fd6ed8a26b5d4b196f06efb868653c1c1"
SRC_URI[cargo-snapshot-powerpc.sha256sum] = "fd832c269af81dd1ee9f3831991fccdc2c7312e9a9069908510eefe7c313d144"
SRC_URI[clippy-snapshot-powerpc.sha256sum] = "c94578d31e4145a4169ab5761537a2a0133818278fb62ee76251ef8ef45228c8"

SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "a54bbe16e6a405f137a0dc51fd570140c80077ba769d29d885a60e74cb3196bb"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "06538b41a9351c449d215498d4ec2f072f728bd18df8fac7ef8534a0d0f34e27"
SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "4d398cd8da17d1a5f26734b39cb17e791b243ac3570f8da2e5e5580a9dfad578"
SRC_URI[clippy-snapshot-powerpc64.sha256sum] = "0abf31a8cb2762a7a81dbee8a5798e43e11a83326711ce00ad16c5587bcf5e49"

SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "7585a20b02b7dd497e393a2e7552a0c6aabb51556fcf7507c6f7ffde530f8c88"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "fcf940c0553a04cb9bd85cce524729c2f54b000b554fee95c7aa218d608b7e3d"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "8fefd2317023716a018986c4a62558a7b543ccf34e4e6d1104afc66edcae1c9c"
SRC_URI[clippy-snapshot-powerpc64le.sha256sum] = "8cc65649396ccad73cecd17b972ab8d4c34837136cf374425be63d697b541469"

SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "4d1106b576221182f84412f3377a5148eab6950d20e45e4274cd8b58df46f26b"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "b28b9c2f183521a204f2899610253c11c86ee4aa903fe66d8410dfaa22c926e6"
SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "7825556908e10a48320cfb2d812851271d7cf549977173e028a0dd23af9d7eac"
SRC_URI[clippy-snapshot-riscv64gc.sha256sum] = "c39c53a82fdaaa97413728a4f3f9feb5b31bdf3e3e563c64651bd01713e79166"

SRC_URI[rustc-snapshot-s390x.sha256sum] = "18caf22fbfc4d26c80b39b4c6b1cd5fb42dba3e32d5d3600c22eae6f688d7f4c"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "3919f6962d0aefdd2cd75d8dba749ed826936065b64c9a8d54d1d85c2685fd66"
SRC_URI[cargo-snapshot-s390x.sha256sum] = "1c6eb1be254574881a611a3b8904cdcfe43c79a285875673e59c890dcd5766c2"
SRC_URI[clippy-snapshot-s390x.sha256sum] = "c87183b52ddb4085ddf2524a775fd255d5fd11ad2fdd6424574f08750a8cf378"

SRC_URI[rustc-snapshot-x86_64.sha256sum] = "4438b809ce4a083af31ed17aeeedcc8fc60ccffc0625bef1926620751b6989d7"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "67be7184ea388d8ce0feaf7fdea46f1775cfc2970930264343b3089898501d37"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "c5c1590f7e9246ad9f4f97cfe26ffa92707b52a769726596a9ef81565ebd908b"
SRC_URI[clippy-snapshot-x86_64.sha256sum] = "02aaff2c1407d2da8dba19aa4970dd873e311902b120a66cbcdbe51eb8836edf"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "f0bddc6dba7d719b8ba131bcd634a8379e00fc825a51c0f17abf424c9cb5c052"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "722238ae13e6e101d1b698d2b3a7915d59bb7f485b594e8d833cce8b9460383b"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
