## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.json
## Rust is self-hosting and bootstraps itself with a pre-built previous version of itself.
## The exact (previous) version that has been used is specified in the source tarball.
## The version is replicated here.

SNAPSHOT_VERSION = "1.69.0"

SRC_URI[cargo-snapshot-aarch64.sha256sum] = "b0ffb368d4e66a5808f96659cc598030761cb06966ae8d5299596b14fbc09364"
SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "c3c5346b1e95ea9bd806b0dd9ff9aa618976fb38f4f3a615af4964bb4dd15633"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "d14166fa0b2832dd87b3f35c08d0bc829f83aa01a539b45df3d923469ee119be"

SRC_URI[cargo-snapshot-i686.sha256sum] = "4c9b1b2fb692bb0c81e524df6368723f061c8bb0d21a1f487eb8e5c2bdf323ab"
SRC_URI[rust-std-snapshot-i686.sha256sum] = "bef330af5bfb381a01349186e05402983495a3e2d4d1c35723a8443039d19a2d"
SRC_URI[rustc-snapshot-i686.sha256sum] = "788ff61555857680d5ea7cf76023f74a7f4820dd1c26abf76536561d4de3cbe6"

SRC_URI[cargo-snapshot-mips.sha256sum] = "bdaef2f95b0485dc2a5cde74c08bd269174bbbb553226c5b5d2287e52841b061"
SRC_URI[rust-std-snapshot-mips.sha256sum] = "89849b93af9d4922554c938bc1c7641498d13c3a8b2b464f3bb7a060eae7a580"
SRC_URI[rustc-snapshot-mips.sha256sum] = "ef17b364df355f7322c1fdd3b4cc4296d2d5d489b58fdd4b12374a1f2975a455"

SRC_URI[cargo-snapshot-mipsel.sha256sum] = "df114ac589fb50fcc3027e26c6e201fc530aadef1bcc8f6396c761a457ba7bbb"
SRC_URI[rust-std-snapshot-mipsel.sha256sum] = "22bb266cf4e6da4d6867144873579b0da47e59c8ec9cb1a329ee8a7f418e8ee3"
SRC_URI[rustc-snapshot-mipsel.sha256sum] = "9df4110f716a309323a776952fcf0dccaae878e7abf0d148c14a2a03c5170850"

SRC_URI[cargo-snapshot-powerpc.sha256sum] = "26c774db5e21ddf66107b677d5c6612d50611186feaa68ff11c34a61e4d5a57e"
SRC_URI[rust-std-snapshot-powerpc.sha256sum] = "6303fc2be557467af0f1ff9cc756fd14ae2b9db7f17f42a1d2238ed934351e56"
SRC_URI[rustc-snapshot-powerpc.sha256sum] = "716e790b7658206c59dc8019c1b603f230c5ce945229463367fade862cea60b9"

SRC_URI[cargo-snapshot-powerpc64.sha256sum] = "160692a0fc5fe1b48b617e063c6ce1d4546e108b32c0049dbde95602a30af133"
SRC_URI[rust-std-snapshot-powerpc64.sha256sum] = "6a4b8b5307141c8207ed67d4fbaf9345c42fb3662e389b9dd3d9a4086c1b6efe"
SRC_URI[rustc-snapshot-powerpc64.sha256sum] = "313d0944a1fc41c1c5ce0a054b3cebccd6f471ce8e8272e5e68f36b8d08e5bb1"

SRC_URI[cargo-snapshot-powerpc64le.sha256sum] = "9a3fe155d763b382ea18753133cb0e2186993e2850568134e2f7e468f2d07197"
SRC_URI[rust-std-snapshot-powerpc64le.sha256sum] = "c44bcf91a9d13412a59ba75e7f8bd14c7f60d44cdb7b21916e502c90b4b378ce"
SRC_URI[rustc-snapshot-powerpc64le.sha256sum] = "1781b5be30fa4fadf7608cc9cd3a2de93b3671a09adfa98ae4d975f789ba2275"

SRC_URI[cargo-snapshot-riscv64gc.sha256sum] = "9bc29f493c353313b968243fbdf5147c9ca401f7f8205aed63f180b5757161e2"
SRC_URI[rust-std-snapshot-riscv64gc.sha256sum] = "8c32a848e2688b2900c3e073da8814ce5649ce6e0362be30d53517d7a9ef21ff"
SRC_URI[rustc-snapshot-riscv64gc.sha256sum] = "03606f1ad3fd196e73e0d255ab6342e4e8fef2edaa1afcc577411aa8a68f0133"

SRC_URI[cargo-snapshot-s390x.sha256sum] = "812907846a454a182b05eab76658b49eabd8d06d2b5e8df56f29d73971f91b03"
SRC_URI[rust-std-snapshot-s390x.sha256sum] = "5c98653199b89bff6f368bed61fca7860f7179364ce9e9474c90af38e2629baf"
SRC_URI[rustc-snapshot-s390x.sha256sum] = "3cef208c0761bc83d156a14a5a94821cc9421772f0f376ea54765a6d00842b21"

SRC_URI[cargo-snapshot-x86_64.sha256sum] = "336eeabf231a7665c26c127a37b8aefffe28cb087c5c8d4ba0460419f5f8eff2"
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "4c95739e6f0f1d4defd937f6d60360b566e051dfb2fa71879d0f9751392f3709"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "70e97ab5b9600328b977268fc92ca4aa53064e4e97468df35215d4396e509279"

SRC_URI[rust-std-snapshot-i586.sha256sum] = "a8125d72e06f2d866472a7aca3bd20a247160171d23a75c4207761a05e00ed5b"

SRC_URI[rust-std-snapshot-sparc64.sha256sum] = "7d50ff8499d8925ea973aa659e1a88190547615b3cfecb79e776ef8de953b755"

SRC_URI += " \
    ${RUST_DIST_SERVER}/dist/${RUST_STD_SNAPSHOT}.tar.xz;name=rust-std-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${RUSTC_SNAPSHOT}.tar.xz;name=rustc-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
    ${RUST_DIST_SERVER}/dist/${CARGO_SNAPSHOT}.tar.xz;name=cargo-snapshot-${RUST_BUILD_ARCH};subdir=rust-snapshot-components \
"

RUST_DIST_SERVER = "https://static.rust-lang.org"

RUST_STD_SNAPSHOT = "rust-std-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
RUSTC_SNAPSHOT = "rustc-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
CARGO_SNAPSHOT = "cargo-${SNAPSHOT_VERSION}-${RUST_BUILD_ARCH}-unknown-linux-gnu"
