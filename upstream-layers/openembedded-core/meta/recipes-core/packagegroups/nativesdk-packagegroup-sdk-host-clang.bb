#
# SPDX-License-Identifier: MIT
#

SUMMARY = "Dependent packages for building external kernel modules using clang"

inherit packagegroup
inherit_defer nativesdk

RDEPENDS:${PN} = "\
    nativesdk-clang \
    nativesdk-lld \
    nativesdk-llvm-bin \
"
