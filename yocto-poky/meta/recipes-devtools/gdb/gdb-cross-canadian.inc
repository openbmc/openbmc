inherit cross-canadian
inherit python-dir

SUMMARY = "GNU debugger (cross-canadian gdb for ${TARGET_ARCH} target)"
PN = "gdb-cross-canadian-${TRANSLATED_TARGET_ARCH}"
BPN = "gdb"

DEPENDS = "nativesdk-ncurses nativesdk-expat nativesdk-gettext \
           virtual/${HOST_PREFIX}gcc-crosssdk virtual/${HOST_PREFIX}binutils-crosssdk virtual/nativesdk-${HOST_PREFIX}libc-for-gcc"

GDBPROPREFIX = "--program-prefix='${TARGET_PREFIX}'"

# Overrides PACKAGECONFIG variables in gdb-common.inc
PACKAGECONFIG ??= "python readline"
PACKAGECONFIG[python] = "--with-python=${WORKDIR}/python,--without-python,nativesdk-python, \
                         nativesdk-python-core nativesdk-python-lang nativesdk-python-re \
                         nativesdk-python-codecs nativesdk-python-netclient"
PACKAGECONFIG[readline] = "--with-system-readline,--without-system-readline,nativesdk-readline"

SSTATE_DUPWHITELIST += "${STAGING_DATADIR}/gdb"

do_configure_prepend() {
cat > ${WORKDIR}/python << EOF
#! /bin/sh
case "\$2" in
        --includes) echo "-I${STAGING_INCDIR}/${PYTHON_DIR}/" ;;
        --ldflags) echo "-Wl,-rpath-link,${STAGING_LIBDIR}/.. -Wl,-rpath,${libdir}/.. -lpthread -ldl -lutil -lm -lpython${PYTHON_BASEVERSION}" ;;
        --exec-prefix) echo "${exec_prefix}" ;;
        *) exit 1 ;;
esac
exit 0
EOF
        chmod +x ${WORKDIR}/python
}

# we don't want gdb to provide bfd/iberty/opcodes, which instead will override the
# right bits installed by binutils.
do_install_append() {
	rm -rf ${D}${exec_prefix}/lib
	cross_canadian_bindirlinks
}

