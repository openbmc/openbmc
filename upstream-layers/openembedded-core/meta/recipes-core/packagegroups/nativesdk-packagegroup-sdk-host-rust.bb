#
# SPDX-License-Identifier: MIT
#

SUMMARY = "Dependent packages for building external rust modules"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup
inherit_defer nativesdk

RDEPENDS:${PN} = "\
    nativesdk-rust-src-lib \
    nativesdk-bindgen-cli \
    nativesdk-libclang \
"
