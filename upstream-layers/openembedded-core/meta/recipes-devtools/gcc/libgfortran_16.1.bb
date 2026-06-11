require recipes-devtools/gcc/gcc-${PV}.inc
require libgfortran.inc

FILES:${PN}-dev += "${libdir}/gcc/${TARGET_SYS}/${BINV}/libcaf*"
