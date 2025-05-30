## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.84.0"

SRC_URI[rustc-snapshot-aarch64.sha256sum] = "9f5650aece53e083b933a57e5a8e0e2db4479f52ec897d5b6d0f77be6cd50498"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "023f0b6153b23ac0e9686c2ab95bc393ee3e295b166bb36de3b4dfb53e3913e0"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "68d4ad239b6d1e810e7b8591636dc408cb2c1e89661329fed906febf9c0a9d98"
SRC_URI[clippy-snapshot-aarch64.sha256sum] = "6022245416a988b9952f36bf20f62de0c2f3337bd150e34439ea6651592ac8ca"

SRC_URI[rustc-snapshot-i686.sha256sum] = "f0bc277e8e8c072d20ee91784f919c08b34a65ab56b67bcd2bb98a009cdcf97c"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "e710100be26e742733d445722a37227381f1ec2010664ece85489bc598d70456"
SRC_URI[cargo-snapshot-i686.sha256sum] = "3a20231475b75f2f6ad0f7c5de283c5706403b41e941d3f1ca6cccfbced214c6"
SRC_URI[clippy-snapshot-i686.sha256sum] = "8b5bdd84b9128abc6d61bc1acb60e55eab90e063c7057371b4e6f79aaa1acb9b"

SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "33ecfb62afce6e30864f07b4e3b76ac6d9f603a3626e5228ad0d59a7ee263cc1"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "763b93ca08028a1e63a8a3754ac6118b2659022538e5e33675f2d7620488cd57"
SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "1531b9f3d09983c43f54d7e5ced6b452044e82b24609532e8cee345656e5d8a4"
SRC_URI[clippy-snapshot-loongarch64.sha256sum] = "80719f10010eaf6a26ba7b2f8c5c7bce3a78fd838cbbe17c00a4e3e7cd45fe5e"

SRC_URI[rustc-snapshot-powerpc.sha256sum] = "ac99e907c9819b61de94a6c12b64256661e5d76c53f3ed25246b46741d1dc973"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "fdbb75060e59ad59273a8ef11fc2ad61837539686df0e8dfe46107e1b15ab90a"
SRC_URI[cargo-snapshot-powerpc.sha256sum] = "dedcb36320cfa924e2d21552057f88ee8a30c0424802015494c2f6237150ec55"
SRC_URI[clippy-snapshot-powerpc.sha256sum] = "de2413ae321d2bccf77c77acb1e9775b63eca1993ee1450e65e388f029565a51"

SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "1f5ff831c9fd97d7d306eb7d99186b9e49f35527144aaabd43f67f04449cd732"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "f2819f8ce1ae41c2c2bcf40fc90d6b1a5d340d3e9558267d464730a797122bf5"
SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "09d9954bf817404c08a8ac952afe1e086ad11fe20d07522a4bde5f26bf7fb5d2"
SRC_URI[clippy-snapshot-powerpc64.sha256sum] = "a34cde35f254210cbb212c3fa85411efaa8e70d600d2c2404b7b319eae9b5be6"

SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "1f6aa28f848b24d4589277b04535ad0258e72b5fa78a4a85ba6d18db95636d69"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "db731e894cce9973ab2b41109c9097d0fc6a49c0d45a0d333bd1547d2c7485a1"
SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "7dc351327de2567cce2a3387f110cce1db20c68fc529353c5cdef2eada5dbfd8"
SRC_URI[clippy-snapshot-powerpc64le.sha256sum] = "b53cb58c719b84044de62851b6214db8d504f57ad71e81b373d7dea0393146d8"

SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "c0eaf824c7789c94901f895792333595fe131e43f097bcf5987301cc1b14c3f0"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "451580f9cdc47eef4efb0986b1960a8f9c4fb0959f81b77eb6292e6bcff9fc64"
SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "3a850b74bdf2c3a5912e9f1ea32aaf1c9ac7c5f89c1c0bcf421b961195833992"
SRC_URI[clippy-snapshot-riscv64gc.sha256sum] = "fb8c9d489e10a961f9faa550980069738b5f0f68740e34d7b3ee46a262b53af7"

SRC_URI[rustc-snapshot-s390x.sha256sum] = "7dbc2fc2c04e820f636bfc74093396c55df0296f7ee4f0547bb849d9a9f05fe0"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "5208f45cec5f3600f2cbc11d1796bc217f09e9c922689c8e4f0cc3f2b83ebb74"
SRC_URI[cargo-snapshot-s390x.sha256sum] = "bdf944ff5c0b97e8b64093fff8d1f63926df86b5e98f91d30c7bd844c6efa20f"
SRC_URI[clippy-snapshot-s390x.sha256sum] = "b02a586369e6edebb0f0608e396472d1867419213fd01ba32f3c847e4c5a3c07"

SRC_URI[rustc-snapshot-x86_64.sha256sum] = "a1737d86f80b31a6d48a6726726275dc068ecb930c9635b13aa59999486de837"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "770237080b9310d126350c3bd70820bd91064c2e96c29ab5f2e002b31b5bd067"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "6c2371488db92a09cd50a1b4045c022f3cf2c643285b3b21105ab5f9b64fd6b6"
SRC_URI[clippy-snapshot-x86_64.sha256sum] = "1db0212c08e126bf6ee28a0d363c5d6859fe357aa9e91d0247059397dbe7e1fd"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "a877622fbbfa58304c34372d369e95db106cc35758ca59053144bf7d86fbf3fc"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "ecd1662d6cbbb62984ab31ae787442f1ab32caf6bc17260d407b3a94b434ad32"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
