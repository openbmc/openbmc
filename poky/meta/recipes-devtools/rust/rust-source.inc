SRC_URI += "https://static.rust-lang.org/dist/rustc-${PV}-src.tar.xz;name=rust"
SRC_URI[rust.sha256sum] = "2d6aa2ea3fedd55acbbd65f78349385f9daa9c14e25db7a8df5d015588eee1cf"

SRC_URI:append:class-target:pn-rust = " \
    file://hardcodepaths.patch \
    file://crossbeam_atomic.patch \
    file://0001-Add-ENOTSUP-constant-for-riscv32-musl.patch"
SRC_URI:append:class-nativesdk:pn-nativesdk-rust = " file://hardcodepaths.patch"

RUSTSRC = "${WORKDIR}/rustc-${PV}-src"

UPSTREAM_CHECK_URI = "https://forge.rust-lang.org/infra/other-installation-methods.html"
UPSTREAM_CHECK_REGEX = "rustc-(?P<pver>\d+(\.\d+)+)-src"
