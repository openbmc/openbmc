require gcc-multilib-config.inc
require gcc-shared-source.inc
#
# Build the list of lanaguages to build.
#
# These can be overridden by the version specific .inc file.

# gcc 3.x expects 'f77', 4.0 expects 'f95', 4.1 and 4.2 expect 'fortran'
FORTRAN ?= ",f77"
LANGUAGES ?= "c,c++${FORTRAN}"

EXTRA_OECONF_BASE ?= ""
EXTRA_OECONF_PATHS ?= ""

GCCMULTILIB ?= "--disable-multilib"
GCCTHREADS ?= "posix"

GCCPIE ??= ""

SYMVERS_CONF ?= "--enable-symvers=gnu"

EXTRA_OECONF = "\
    ${@['--enable-clocale=generic', ''][d.getVar('USE_NLS') != 'no']} \
    --with-gnu-ld \
    --enable-shared \
    --enable-languages=${LANGUAGES} \
    --enable-threads=${GCCTHREADS} \
    ${GCCMULTILIB} \
    ${GCCPIE} \
    --enable-c99 \
    --enable-long-long \
    ${SYMVERS_CONF} \
    --enable-libstdcxx-pch \
    --program-prefix=${TARGET_PREFIX} \
    --without-local-prefix \
    --disable-install-libiberty \
    ${EXTRA_OECONF_BASE} \
    ${EXTRA_OECONF_GCC_FLOAT} \
    ${EXTRA_OECONF_PATHS} \
    ${@get_gcc_mips_plt_setting(bb, d)} \
    ${@get_gcc_ppc_plt_settings(bb, d)} \
    ${@get_gcc_multiarch_setting(bb, d)} \
"

# glibc version is a minimum controlling whether features are enabled. 
# Doesn't need to track glibc exactly
EXTRA_OECONF:append:libc-glibc = " --with-glibc-version=2.28 "

# Set this here since GCC configure won't auto-detect and enable
# initfini-arry when cross compiling.
EXTRA_OECONF:append = " --enable-initfini-array"

export gcc_cv_collect2_libs = 'none required'
# We need to set gcc_cv_collect2_libs else there is cross-compilation badness
# in the config.log files (which might not get generated until do_compile
# hence being missed by the insane do_configure check).

EXTRA_OECONF:append:linux = " --enable-__cxa_atexit"

EXTRA_OECONF:append:mips64 = " --with-abi=64 --with-arch-64=mips64 --with-tune-64=mips64"
EXTRA_OECONF:append:mips64el = " --with-abi=64 --with-arch-64=mips64 --with-tune-64=mips64"
EXTRA_OECONF:append:mips64n32 = " --with-abi=64 --with-arch-64=mips64 --with-tune-64=mips64"
EXTRA_OECONF:append:mips64eln32 = " --with-abi=64 --with-arch-64=mips64 --with-tune-64=mips64"
EXTRA_OECONF:append:mipsisa32r6el = " --with-abi=32 --with-arch=mips32r6"
EXTRA_OECONF:append:mipsisa32r6 = " --with-abi=32 --with-arch=mips32r6"
EXTRA_OECONF:append:mipsisa64r6el = " --with-abi=64 --with-arch-64=mips64r6"
EXTRA_OECONF:append:mipsisa64r6 = " --with-abi=64 --with-arch-64=mips64r6"

EXTRA_OECONF_GCC_FLOAT ??= ""
CPPFLAGS = ""

SYSTEMHEADERS = "${target_includedir}"
SYSTEMLIBS = "${target_base_libdir}/"
SYSTEMLIBS1 = "${target_libdir}/"

do_configure:prepend () {
	# teach gcc to find correct target includedir when checking libc ssp support
	mkdir -p ${B}/gcc
	echo "NATIVE_SYSTEM_HEADER_DIR = ${SYSTEMHEADERS}" > ${B}/gcc/t-oe
	cat ${S}/gcc/defaults.h | grep -v "\#endif.*GCC_DEFAULTS_H" > ${B}/gcc/defaults.h.new
	cat >>${B}/gcc/defaults.h.new <<_EOF
#define NATIVE_SYSTEM_HEADER_DIR "${SYSTEMHEADERS}"
#define STANDARD_STARTFILE_PREFIX_1 "${SYSTEMLIBS}"
#define STANDARD_STARTFILE_PREFIX_2 "${SYSTEMLIBS1}"
#define SYSTEMLIBS_DIR "${SYSTEMLIBS}"
#endif /* ! GCC_DEFAULTS_H */
_EOF
	mv ${B}/gcc/defaults.h.new ${B}/gcc/defaults.h
}

do_configure () {
	# Setup these vars for cross building only
	# ... because foo_FOR_TARGET apparently gets misinterpreted inside the
	# gcc build stuff when the build is producing a cross compiler - i.e.
	# when the 'current' target is the 'host' system, and the host is not
	# the target (because the build is actually making a cross compiler!)
	if [ "${BUILD_SYS}" != "${HOST_SYS}" ]; then
		export CC_FOR_TARGET="${CC}"
		export GCC_FOR_TARGET="${CC}"
		export CXX_FOR_TARGET="${CXX}"
		export AS_FOR_TARGET="${HOST_PREFIX}as"
		export LD_FOR_TARGET="${HOST_PREFIX}ld"
		export NM_FOR_TARGET="${HOST_PREFIX}nm"
		export AR_FOR_TARGET="${HOST_PREFIX}ar"
		export GFORTRAN_FOR_TARGET="gfortran"
		export RANLIB_FOR_TARGET="${HOST_PREFIX}ranlib"
	fi
	export CC_FOR_BUILD="${BUILD_CC}"
	export CXX_FOR_BUILD="${BUILD_CXX}"
	export CFLAGS_FOR_BUILD="${BUILD_CFLAGS}"
	export CPPFLAGS_FOR_BUILD="${BUILD_CPPFLAGS}"
	export CXXFLAGS_FOR_BUILD="${BUILD_CXXFLAGS}"
	export LDFLAGS_FOR_BUILD="${BUILD_LDFLAGS}"
	export CFLAGS_FOR_TARGET="${TARGET_CFLAGS}"
	export CPPFLAGS_FOR_TARGET="${TARGET_CPPFLAGS}"
	export CXXFLAGS_FOR_TARGET="${TARGET_CXXFLAGS}"
	export LDFLAGS_FOR_TARGET="${TARGET_LDFLAGS}"


	oe_runconf
}
