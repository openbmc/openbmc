## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.78.0"

SRC_URI[cargo-snapshot-aarch64.sha256sum] = "5173f84a07d4cc6b19f27eda7464999c5886232ce8e54bf61b06617635d43fb9"
SRC_URI[clippy-snapshot-aarch64.sha256sum] = "086367aa83ea8c30ce41fb6c097ae6ebdc92363fb1ce8ee9f01d44cafa625f61"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "66cf114dcd8056a596bf169f824ff95ff1837bc065daaafdbcaa02ce92903304"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "e0450bef5537e6b0bb82872e9f837c3b3f6397cc8eba6f53211481c82426e1ce"

SRC_URI[cargo-snapshot-i686.sha256sum] = "472230d0a04c6a2db80671549cb970176299817883dafbffb15c993f5aeaa909"
SRC_URI[clippy-snapshot-i686.sha256sum] = "1abc63d39850194580c22f998ff14aade2601f7cfc2465cf68828dcfc9d5536d"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "9e5e02311853354c1540e1b4da6c1686616cea9d3a233c033023f36d950977c0"
SRC_URI[rustc-snapshot-i686.sha256sum] = "8ba9c213e5daaf08a13c5787a4ea0d7ae8d1e7004126de2b8dc6a6dcd798becd"

SRC_URI[cargo-snapshot-loongarch64.sha256sum] = "fabe8c461fb87eff8bdf7845fca7051c5dc6e46da4dc6b3023c90efc7d7bd918"
SRC_URI[clippy-snapshot-loongarch64.sha256sum] = "d9366b69bd41673bccd7617c2f3fe6a1cdd478fda407c7d9fda0021e33deb825"
SRC_URI[rust-std-snapshot-loongarch64.sha256sum] = "9f5dc7bfd8602a594157d04c480fb74804a3cdd9b77f46202e289a546af7c846"
SRC_URI[rustc-snapshot-loongarch64.sha256sum] = "9f19f781cdfa51f5e8b540602a6a07d45ab5f2e063a86af6177625ecf9d79358"

SRC_URI[cargo-snapshot-powerpc.sha256sum] = "3d73d16f1b7c4aaf53e6d9ac6d04586c60698b0c2c793c55585b0234f75d247d"
SRC_URI[clippy-snapshot-powerpc.sha256sum] = "f9b0d7ac6bdd26b678201d068f8777df5fdc7f524602f73eff54c7e8a5844892"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "4ac9958386c4225b14f8e6281dd64a2b00d5db32c5b336fac137c7c729df25a0"
SRC_URI[rustc-snapshot-powerpc.sha256sum] = "33bbd45416ce00f2e21073ebcef8668575a3955189c85a13172bf5f5750e00ef"

SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "271e121aa1324d37d2bd650217256a792ed387af4b14df0b2757ba22dab50ee5"
SRC_URI[clippy-snapshot-powerpc64.sha256sum] = "53294b52155bb320570dd125e58b07ee3a0cb35e47a33716b3561d1751ccc538"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "303e3533e72099d4f0a1d5c2f31c68ef93b3a3ef8af5d36773eac394f920fb22"
SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "338299628336aa97e6d2f30b5d3c4beaf78038f1e1347a378cbbf80d02a461ea"

SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "b58d7e61fb36295ebd0a09242e292c7bc4fa24fc189aa285b6e266263f356316"
SRC_URI[clippy-snapshot-powerpc64le.sha256sum] = "5b2b621b79a7635d5670bc17d992f6a0bff0e153b6629fdd501a08cf0df7b126"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "117a01afab9d4c6e39bc32b57f8a4b9ad8d693869dde0c52fa51a6ec05eaab8b"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "45ba3091f9f17abfb01d0c03c87e8de33f0a210aefbdb007905dbf6cfba8590d"

SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "fc3fda5412f2b1a53c6a3fe6c0a98864d50eac8c63e9b38383085abad6487e3b"
SRC_URI[clippy-snapshot-riscv64gc.sha256sum] = "44745c59092bc449b0338ab6788eb0b2a6d5ad382224fe1ba1017931ebb6e6dc"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "e5d0e1e532f4ca4656d971c4301e87cbcd571daeffd7830b1d02055f95bdaca6"
SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "6925c93e4b5b21e37b27aed7723cdd6da317eec3f06f86fb0e5be868f58ea052"

SRC_URI[cargo-snapshot-s390x.sha256sum] = "59da021df10c62786d33bc6b186a7d1114f788bebcd09f0dd323032fad2f7f5d"
SRC_URI[clippy-snapshot-s390x.sha256sum] = "1a5542ac30cbcfb15512aba2d53b30dd8835058514085067bbcf7c938c05b79e"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "aee53a0a04fb44c95b9aa6217c5483113e1f67fa595dea6fdcbbbd72826e729b"
SRC_URI[rustc-snapshot-s390x.sha256sum] = "1f458a0b3b947d78d50c67b16bb68e8528cb71d52a7021c3b1652b5f9004c40b"

SRC_URI[cargo-snapshot-x86_64.sha256sum] = "f8aacf7a101eb10dc000b8bf26de90a9d0ce678d02ccf70430ed20dd31ecec6b"
SRC_URI[clippy-snapshot-x86_64.sha256sum] = "b15184ba6078dd7976f06239a258313a96de2677b37e4c560779cf7f261c3ee5"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "95aece42a336f237c5bac5c5d9aca051b7f0bd3e6a64fb3c5828e6d0d3af2e8c"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "3994971e5923716d54e4b574ce238f04c4e20cda03990670f7cc3f87d36e5499"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "c119866af24c91108d34267e24886ad90fd8784edfdfd0d36c64013b06974f71"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "7e158a16f733844975e65090b07706596401719bd3f5aca44da9f98fb3c11b04"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"
RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
