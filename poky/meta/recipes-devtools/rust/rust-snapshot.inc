## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.73.0"

SRC_URI[cargo-snapshot-aarch64.sha256sum] = "1195a1d37280802574d729cf00e0dadc63a7c9312a9ae3ef2cf99645f7be0a77"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "cbaa2549b3accc63b424251fadc3a66d922541df22e736a355246d81998f4252"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "628f57a45eb8143a7ac1acd5d6d01e3ae3cdf1ad11d151795ed765f6e5f3047e"

SRC_URI[cargo-snapshot-i686.sha256sum] = "8780f10eb3565b47f2616ccc1616c1a491a12a055976a25de551cb29e7f50390"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "c46936bf3c921c90593b6ea77a08b1ec8b240c8184b287fd89fea636ac437b25"
SRC_URI[rustc-snapshot-i686.sha256sum] = "3d98a27c50ae79de7c7956506d649e169312e76b4fc2314a975b45ebd1dea5b9"

SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "8e4766b19147d99670e5e3473981967d94465ad550c70c830ee86425260e1a05"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "533d38377ea362ba83a3eba5008fb89466f4094b247cb1510d4752d0bbbcc070"
SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "0fee28452598859780cf871d3f5a809ca606a840c843a8735248c49dbbd32b1c"

SRC_URI[cargo-snapshot-powerpc.sha256sum] = "ffa30a6e480b3e20cf42ca32266edcfd0b8e4f1b84da450937e4bca7ec3e88e8"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "bed705726b356d318a0fc34bbf1a1bc4cacb61bfc20889bdfb77d3aaebbd406a"
SRC_URI[rustc-snapshot-powerpc.sha256sum] = "2ed995d178158b8447b68c8642cffed2402fc7502c0d5475b7c9fdfa895dd037"

SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "35454cd3778dac739da6a3fbbe278a2b6818bc4ca95600412e0d51c13f3fdc1f"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "c7bb65f4b7b59f65e4db8a0877192f59dce12021c866e2ef86014d99d18a23f5"
SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "9458ea1308e88ceb88dbc38d9d4918dc665d3c148a39789de417b0668fc45a3f"

SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "14f74b06ddeebbec48098828d2de55e631ea4cacd7c7ebad8a96220d1a470e0a"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "a74d300dbf1fbf2d4af80995db19501211e2bd25572ab45748b48df4448d4656"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "c52d6f28d370e7bd30e07655d534a3aad21afc6c32f0c80e8a0f7249d2b86b29"

SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "50d42893e2da56657a7c00ed17498417628793d8b2bceab829be7b2575478879"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "8b58ae17468f2f2768b470f532a1ba9668dc778c242cc9dfc022ea1ebc962f63"
SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "fee4b06850574de4821d335e622ca0607753e042848ba00ada826f8c8ca4b44a"

SRC_URI[cargo-snapshot-s390x.sha256sum] = "6035a925f3307c98d2caf0b1727fc401e7a64a09e6a7132a0cd882937720bda2"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "545b97978470135f8726fb82970acb882b9d57a724c2462ee90efee47429fc84"
SRC_URI[rustc-snapshot-s390x.sha256sum] = "0ca1d450f10f2d87b630c5a19f3aad13f0e39aec63f253654ad9f68c7bf1872e"

SRC_URI[cargo-snapshot-x86_64.sha256sum] = "7c3ce5738d570eaea97dd3d213ea73c8beda4f0c61e7486f95e497b7b10c4e2d"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "96efb163a57b400152c357be0ea3a0dd902b56cc0df662b9ac951403c7c7b15b"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "14f383eb4d6e65ce01cc99f2c5cf5a78744239f29704f72fe84f11095af779f5"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "02cfcb9bae68c406b5cab9aad74ac8631fa4fdd9246aac2e4d4c0aeafbcad42a"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "717846830d95a689fb44dbafc4f4e09285bf7bc2c37bcc70f6d17785c21f4569"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
