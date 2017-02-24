require recipes-devtools/gcc/gcc-${PV}.inc
require gcc-runtime.inc

FILES_libgomp-dev += "\
    ${libdir}/gcc/${TARGET_SYS}/${BINV}/include/openacc.h \
"

