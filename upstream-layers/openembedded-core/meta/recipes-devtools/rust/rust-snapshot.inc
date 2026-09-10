## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.97.1"

SRC_URI[rustc-snapshot-aarch64.sha256sum] = "b344b81f0cd4c2246c7da8b197fe7a339d7dd02bb15cb69b2524115d9c75224c"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "46aed8e63186350004d8ec6afca798811e6530b514352e5a8a26f3dc4939b3be"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "8f70bcaccea5ba4db187c3fd4d64e24592b4e16af513497201f5909d61691dbe"
SRC_URI[clippy-snapshot-aarch64.sha256sum] = "d8bac7b0ba5ca9bb868ccb9e367a1d52f4837f3ebf4892eaf64cda37ce362bb5"

SRC_URI[rustc-snapshot-i686.sha256sum] = "c096e21362fe1390e7aefc731671bce208a43828a83bcc32a35ffb8302f5802e"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "7f4be0697e96dd81f75f66f0e25a51d595223e7c58af6675810f066cab6ef584"
SRC_URI[cargo-snapshot-i686.sha256sum] = "b76d87a7a550c51c48d4e9396cec29917b2e8fcac7b7ffeb6da01b22e2b723dc"
SRC_URI[clippy-snapshot-i686.sha256sum] = "97d20d083e3475bb50898e399cf676d286763ecd978b2fafb57d5d582af27fdf"

SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "140c1dd5973d42fb1503b1d3bdc6da23a628b07137fc126998ae7f0f41b80a30"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "8b7cbf62dcc4a65da80680f9ae6a15d9934ba1fc30a689dbe53de99f6e4622a7"
SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "41f0ebc698f24fef6989d0e31e64d2377ba09d66ad5aba17d88634bd6aea0568"
SRC_URI[clippy-snapshot-loongarch64.sha256sum] = "c48fcbae8a844db00948b0c46527fb701582c8b867ee908d8bdf145953572bce"

SRC_URI[rustc-snapshot-powerpc.sha256sum] = "929b9ab78cb8aef161420b33592b070516b09f27fdc923dd4153318280449074"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "9c3286cc2aab1868f693cd458d5d7bf8c307aaf8809e9a2dddbfc31ece94629f"
SRC_URI[cargo-snapshot-powerpc.sha256sum] = "673caa54fdbf5477ab18bbc20e7c580f0f6179a772589abf2d22d3030a94daa2"
SRC_URI[clippy-snapshot-powerpc.sha256sum] = "fd3061d0f7e31e515fc615d44243588b59adaf0743980477e35bc96e48247396"

SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "a52596a0fbdb41c4f6874ae7bfdeeb0197fb1227f185d897e040cd908de48d63"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "58675598a59db1524e934a27f279b6669d65e91f53675d022fd5e32046c2742c"
SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "48ee9abeaa117a1663c3ef3a1771f808cd7d64d86db5d2904d10d8aa829b96e8"
SRC_URI[clippy-snapshot-powerpc64.sha256sum] = "48b6494a0b5d5cc239827c05c46444ad3e2039e11aba3efff1ad4aa6073e9550"

SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "5ec2a5477d961114b735e43138ddee914ce12be225b8a5bfbfadb8ca966bd8f3"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "ab2fb5c9dd7d3a0987c97c9f56732d013a11796fcd1510651570ae2fcc512bb2"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "fcdcda6e055a77ea7fbe6d74e30d3949a0501116c08966a97fd728dc3bf5872e"
SRC_URI[clippy-snapshot-powerpc64le.sha256sum] = "df0d9c12645b006016fbf12634f4b00c54182620907808d11991bf1bc234d7a2"

SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "3cb19f130eb83b56277c526f737ce02dedf6d38145f5220ff66af5b47c499dcb"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "5cec88477a70fe83c10a12efd6bdad1b0f8dd4c1ebba1e427f00c69666c5b6f9"
SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "df0d9edd65f4f7710a1a49a764a3da84e7a95467ded15dade41bbdf74051743b"
SRC_URI[clippy-snapshot-riscv64gc.sha256sum] = "d7c1ed5a12a028e0a12134283136f24787597f879ad4e9797aedab5f54b40a5e"

SRC_URI[rustc-snapshot-s390x.sha256sum] = "05103f9a7c658bf2b16bb810879c0f73e4123ac8ac066f66e277d6809468fe9c"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "6c6b2b285225f3551250bd4c5e05293a0839fc18d1625c545b81fbea9f82c31b"
SRC_URI[cargo-snapshot-s390x.sha256sum] = "3236fef696f28c21e754dbd78de2772e8b418b7086a4511870e8e469e7ccb572"
SRC_URI[clippy-snapshot-s390x.sha256sum] = "8d0a6a7759451cdbf957cda0527579b59ef1b2a7600b65af05257a7d2aea6360"

SRC_URI[rustc-snapshot-x86_64.sha256sum] = "9819d0a32d56bd339585319c80260e332779f5541fd66838ab7e016d6c814819"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "1c1e704ae80126b7de34f72ea2825f7fd01736dec20732faed47374b95282fba"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "e1be5f5ff7f7f80ca506fb65770b759edbdc6d303781ed71c5de8ec8a8394779"
SRC_URI[clippy-snapshot-x86_64.sha256sum] = "3441df8fb54db985f8c8a3e8356b8874a3f92cc8cca8565cfe36f1dc15935e72"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "a3090f555a9c3fd91d3c8b73152eee007fa348d20a8d026ea577a467acf5e7c7"

SRC_URI[rust-std-snapshot-riscv64a23.sha256sum] = "1600b3358f88c8a80dc1868b6d98ca899fcb1b2b9c512fdc8b832928e7a37875"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "be689309aae0247e0a58a87b6ed69c28573ec5ec3f56766cb8b0b3066a3a0a70"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
