require boost-${PV}.inc
require boost.inc

SRC_URI += "file://boost-CVE-2012-2677.patch \
           file://boost-math-disable-pch-for-gcc.patch \
           file://0001-Don-t-set-up-arch-instruction-set-flags-we-do-that-o.patch \
           file://0001-dont-setup-compiler-flags-m32-m64.patch \
           file://de657e01635306085488290ea83de541ec393f8b.patch \
           file://0001-futex-fix-build-on-32-bit-architectures-using-64-bit.patch \
           file://0001-Don-t-skip-install-targets-if-there-s-build-no-in-ur.patch \
           "
