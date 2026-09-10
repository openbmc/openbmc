#
# SPDX-License-Identifier: MIT
#

SUMMARY = "Essential build dependencies for building rust kernel modules in image"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

RDEPENDS:${PN} = "\
    bindgen-cli \
    libclang \
    rust \
    rust-src-lib \
    rust-tools-rustfmt \
"

RDEPENDS:${PN}:append:riscv64 = "\
    clang \
    lld \
    llvm-bin \
"
