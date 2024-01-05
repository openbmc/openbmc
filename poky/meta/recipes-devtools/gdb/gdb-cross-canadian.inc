inherit cross-canadian
inherit python3-dir
inherit pkgconfig

SUMMARY = "GNU debugger (cross-canadian gdb for ${TARGET_ARCH} target)"
PN = "gdb-cross-canadian-${TRANSLATED_TARGET_ARCH}"
BPN = "gdb"

DEPENDS = "nativesdk-ncurses nativesdk-expat nativesdk-gettext nativesdk-gmp nativesdk-mpfr \
           virtual/${HOST_PREFIX}gcc virtual/${HOST_PREFIX}binutils virtual/nativesdk-libc"

GDBPROPREFIX = "--program-prefix='${TARGET_PREFIX}'"

# Overrides PACKAGECONFIG variables in gdb-common.inc
PACKAGECONFIG ??= "python readline ${@bb.utils.filter('DISTRO_FEATURES', 'debuginfod', d)}"
PACKAGECONFIG[python] = "--with-python=${WORKDIR}/python,--without-python,nativesdk-python3, \
                         nativesdk-python3-core \
                         nativesdk-python3-codecs nativesdk-python3-netclient \
                         "
PACKAGECONFIG[readline] = "--with-system-readline,--without-system-readline,nativesdk-readline"
PACKAGECONFIG[debuginfod] = "--with-debuginfod, --without-debuginfod, nativesdk-elfutils"

SSTATE_ALLOW_OVERLAP_FILES += "${STAGING_DATADIR}/gdb"

do_configure:prepend() {
cat > ${WORKDIR}/python << EOF
#! /bin/sh
case "\$2" in
        --includes) echo "-I${STAGING_INCDIR}/${PYTHON_DIR}${PYTHON_ABI}/" ;;
        --ldflags) echo "-Wl,-rpath-link,${STAGING_LIBDIR}/.. -Wl,-rpath,${libdir}/.. -lpthread -ldl -lutil -lm -lpython${PYTHON_BASEVERSION}${PYTHON_ABI}" ;;
        --exec-prefix) echo "${exec_prefix}" ;;
        *) exit 1 ;;
esac
exit 0
EOF
        chmod +x ${WORKDIR}/python
}

# we don't want gdb to provide bfd/iberty/opcodes, which instead will override the
# right bits installed by binutils.
do_install:append() {
	rm -rf ${D}${exec_prefix}/lib
	cross_canadian_bindirlinks
}
