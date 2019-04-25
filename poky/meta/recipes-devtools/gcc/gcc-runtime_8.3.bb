require recipes-devtools/gcc/gcc-${PV}.inc
require gcc-runtime.inc

# Disable ifuncs for libatomic on arm conflicts -march/-mcpu
EXTRA_OECONF_append_arm = " libat_cv_have_ifunc=no "

FILES_libgomp-dev += "\
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/openacc.h \
"

# Building with thumb enabled on armv6t fails
ARM_INSTRUCTION_SET_armv6 = "arm"
