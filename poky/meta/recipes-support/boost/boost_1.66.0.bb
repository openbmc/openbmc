require boost-${PV}.inc
require boost.inc

SRC_URI += "\
    file://arm-intrinsics.patch \
    file://boost-CVE-2012-2677.patch \
    file://boost-math-disable-pch-for-gcc.patch \
    file://0001-Apply-boost-1.62.0-no-forced-flags.patch.patch \
    file://0003-Don-t-set-up-arch-instruction-set-flags-we-do-that-o.patch \
    file://0002-Don-t-set-up-m32-m64-we-do-that-ourselves.patch \
"
