SRC_URI += "https://static.rust-lang.org/dist/rustc-${PV}-src.tar.xz;name=rust"
SRC_URI[rust.sha256sum] = "e8170d318fac9d2fc17d5c3e648e7068f56e8db8d233d864aeffbef7c6542eac"

SRC_URI:append:class-target:pn-rust = " \
    file://hardcodepaths.patch \
    file://crossbeam_atomic.patch \
    file://0001-Add-ENOTSUP-constant-for-riscv32-musl.patch"
SRC_URI:append:class-nativesdk:pn-nativesdk-rust = " file://hardcodepaths.patch"

RUSTSRC = "${WORKDIR}/rustc-${PV}-src"

UPSTREAM_CHECK_URI = "https://forge.rust-lang.org/infra/other-installation-methods.html"
UPSTREAM_CHECK_REGEX = "rustc-(?P<pver>\d+(\.\d+)+)-src"
