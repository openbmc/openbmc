include boost-${PV}.inc
include boost.inc

SRC_URI += "\
    file://arm-intrinsics.patch \
    file://0001-Do-not-qualify-fenv.h-names-that-might-be-macros.patch;striplevel=2 \
    file://consider-hardfp.patch \
"
