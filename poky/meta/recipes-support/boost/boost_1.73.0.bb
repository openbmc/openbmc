require boost-${PV}.inc
require boost.inc

SRC_URI += "file://arm-intrinsics.patch \
           file://boost-CVE-2012-2677.patch \
           file://boost-math-disable-pch-for-gcc.patch \
           file://0001-Apply-boost-1.62.0-no-forced-flags.patch.patch \
           file://0001-Don-t-set-up-arch-instruction-set-flags-we-do-that-o.patch \
           file://0001-dont-setup-compiler-flags-m32-m64.patch \
           file://0001-Make-index-detail-rtree-visitors-insert-base-class-p.patch \
           "
