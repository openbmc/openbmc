## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.79.0"

SRC_URI[cargo-snapshot-aarch64.sha256sum] = "4ca5e9bd141b0111387ea1aa0355f87eb8d0da52fbc616cefa4ecde4997aa65b"
SRC_URI[clippy-snapshot-aarch64.sha256sum] = "77803cfff2ea0342f26b59eabec353bc43a1791012aa70855ecfea0fb7ae76ac"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "519abf4757fbd8d7e3bb4e4cfdc362ded972c1d95f04675684df2d31e8c0899b"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "9c847b42b81325d25a9240e33bf03fa8652f5dd321ae90a9a7a58b46bf124b17"

SRC_URI[cargo-snapshot-i686.sha256sum] = "57bd43f363ebd399a38aa4ac3b137e9172706770f8b23479f9dd859c6ae779ab"
SRC_URI[clippy-snapshot-i686.sha256sum] = "1d2612c829d946ca8ce32f2beacfde2c5ebed1fcacb720a58fb8e7786e4c82f2"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "158294f7c424ef75d84046b573f225c15d531745cc79d416a18c11aa8253830e"
SRC_URI[rustc-snapshot-i686.sha256sum] = "4695a6eb66252d519510ca0a859803fff3a5041f71004404df0ccc9fbf0298d5"

SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "354d498a8e36abe6ebd7bd859c8c7e7f02b029aa1897b9b60a87e9902ebb1079"
SRC_URI[clippy-snapshot-loongarch64.sha256sum] = "88eab562705d56a246a09f2897cbae9e37a66dc3cc2cab52c781cd78024f2b52"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "88730b1b0d9c5252e041ed1fc2375d7292dc45132bcc8b6214490c578de240d3"
SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "f39a604e1a5a5ba1c7342c9d98c9952b5ba252cda4b55cc6ae7bd6fca72f3889"

SRC_URI[cargo-snapshot-powerpc.sha256sum] = "2a55e97ce512c27a012720bb2a0cd86554c1d734b5cb91b98f58af167cc0fde1"
SRC_URI[clippy-snapshot-powerpc.sha256sum] = "1ae0bbe5835d9461a12f22d509787e6c11aac8335c677152a57a04f8d22cf755"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "6aa2f351bc7dccb544781f4fecddb5ef67e05bb310c84e0106d968231384d9f4"
SRC_URI[rustc-snapshot-powerpc.sha256sum] = "3b9a8067e97dd269a27fe91be163e046e85b47973f5d512c3368c49d332dacb1"

SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "1e2ffcc41a2083be2f4733d534d5ee0e05fc2ace1f21033792a4e2497cbbfbe4"
SRC_URI[clippy-snapshot-powerpc64.sha256sum] = "0bceeb8dc5b48e5ea9cff67cdcf8488fe2afe44d7e75ca04669c7c0c2e02a0df"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "af95929af71494ee3e6988ead07204ea05b6f2b729cdb1ffcc26db75e47bf318"
SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "2295d50425ed5dfb028560ac0f9a611212d8deb43791a1d199419043157ab95e"

SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "bec32adad9f2cd1650ed24ee82fccf7329c68243cdef7cef1e720ccaea097dab"
SRC_URI[clippy-snapshot-powerpc64le.sha256sum] = "6bb12041b6171e80598c0c04eb7e9bd07ab4fb4d654a9289618e3288a4d2f6c8"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "3b5d53d70837d5ec942ec41a8da37ed932bbdc1e53f78dc54e218db458701588"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "3b144840f179114cde3078909370bfb6928153d0ae303ac9848622c57cee3578"

SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "83e28f3e02cce4a9d19841bcb40ec0c8ad4e9af9fb215c39876028e6e9e6baf1"
SRC_URI[clippy-snapshot-riscv64gc.sha256sum] = "fbc978db4be2e6fdb1dbcb3ca00ee54c44dafd08930738f1c6aa7ae249685a34"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "2b8af4060acb10f347c66a3477ae92151beba12192590ea40723dcf9b1f7f5b2"
SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "1b952b8cd597060ac07bbe99cea382651ca6eee4ac2abe0a90303ccf2278e60e"

SRC_URI[cargo-snapshot-s390x.sha256sum] = "5663e6ea018a13f1c87ac74de267829952ea979fbe5f8ee5c1c3531e25d1749c"
SRC_URI[clippy-snapshot-s390x.sha256sum] = "c00bd892e0f29bbc2514f87c7521210ced8c175a41eee0a428e19a6ef157779f"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "d70e710a5138871e8d972f195ced9f7aa639ccef23a87fa03cc527d2b9c3cd36"
SRC_URI[rustc-snapshot-s390x.sha256sum] = "ef4b494fb7356b6e88a69b71378c04ae0f4a3c264adae84c9d4f3a1314eaa2b4"

SRC_URI[cargo-snapshot-x86_64.sha256sum] = "07fcadd27b645ad58ff4dae5ef166fd730311bbae8f25f6640fe1bfd2a1f3c3c"
SRC_URI[clippy-snapshot-x86_64.sha256sum] = "3fb282ee97626e4f25c4f6faac3997859b89f13983dd6fa111e25182dfcb91fa"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "2c914483c0882d44af2e50a181cbd2c953d672d50b31aa669ee2346cade1f108"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "a04cf42022d0a5faf01c31082bfb1dde9c38409f0ca6da90a3e40faa03e797ae"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "9c38f2cd4c19196fcf113166237ab03512bf19ce5a7f13c3bfe0e5cc3317eb44"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "f7c7a73a8e5640f9d5aca6b26866b4da08cf3d3509ed1c4184309fb868acbff2"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"
RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
