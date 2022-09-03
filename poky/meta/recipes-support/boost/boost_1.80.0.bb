require boost-${PV}.inc
require boost.inc

SRC_URI += "file://boost-math-disable-pch-for-gcc.patch \
           file://0001-Don-t-set-up-arch-instruction-set-flags-we-do-that-o.patch \
           file://0001-dont-setup-compiler-flags-m32-m64.patch \
           file://0001-The-std-lib-unary-binary_function-base-classes-are-d.patch \
           "
