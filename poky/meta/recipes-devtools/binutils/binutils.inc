SUMMARY = "GNU binary utilities"
DESCRIPTION = "The GNU Binutils are a collection of binary tools. \
The main ones are ld (GNU Linker), and as (GNU Assembler). This \
package also includes addition tools such as addr2line (Converts \
addresses into filenames and line numbers), ar (utility for creating, \
modifying and extracting archives), nm (list symbols in object \
files), objcopy (copy and translate object files), objdump (Display \
object information), and other tools and related libraries."
HOMEPAGE = "http://www.gnu.org/software/binutils/"
BUGTRACKER = "http://sourceware.org/bugzilla/"
SECTION = "devel"
LICENSE = "GPLv3"

DEPENDS = "flex-native bison-native zlib-native gnu-config-native autoconf-native"

inherit autotools gettext multilib_header texinfo

FILES_${PN} = " \
	${bindir}/${TARGET_PREFIX}* \
	${libdir}/lib*.so.* \
	${libdir}/lib*-${PV}*.so \
	${prefix}/${TARGET_SYS}/bin/* \
        ${bindir}/embedspu"

RPROVIDES_${PN} += "${PN}-symlinks"

FILES_${PN}-dev = " \
	${includedir} \
	${libdir}/*.la \
	${libdir}/libbfd.so \
	${libdir}/libctf.so \
	${libdir}/libctf-nobfd.so \
	${libdir}/libopcodes.so"

# Rather than duplicating multiple entries for these, make one
# list and reuse it.

LDGOLD_ALTS ?= "ld.gold dwp"
LDGOLD_ALTS_riscv64 = ""
LDGOLD_ALTS_riscv32 = ""
LDGOLD_ALTS_libc-glibc_mipsarch = ""

USE_ALTERNATIVES_FOR = " \
	addr2line \
	ar \
	as \
	c++filt \
	elfedit \
	gprof \
	ld \
	ld.bfd \
	${LDGOLD_ALTS} \
	nm \
	objcopy \
	objdump \
	ranlib \
	readelf \
	size \
	strings \
	strip \
"

python do_package_prepend() {
    make_alts = d.getVar("USE_ALTERNATIVES_FOR") or ""
    prefix = d.getVar("TARGET_PREFIX")
    bindir = d.getVar("bindir")
    for alt in make_alts.split():
        d.setVarFlag('ALTERNATIVE_TARGET', alt, bindir + "/" + prefix + alt)
        d.setVarFlag('ALTERNATIVE_LINK_NAME', alt, bindir + "/" + alt)
}

B = "${S}/build.${HOST_SYS}.${TARGET_SYS}"

EXTRA_OECONF = "--program-prefix=${TARGET_PREFIX} \
                --disable-werror \
                --enable-deterministic-archives \
                --enable-plugins \
                --disable-gdb \
                --disable-gdbserver \
                --disable-libdecnumber \
                --disable-readline \
                --disable-sim \
                ${LDGOLD} \
                ${EXTRA_TARGETS} \
                ${@bb.utils.contains('DISTRO_FEATURES', 'multiarch', '--enable-64-bit-bfd', '', d)}"

EXTRA_TARGETS = ""
EXTRA_TARGETS_x86-64 = " --enable-targets=x86_64-pe,x86_64-pep "
EXTRA_TARGETS_class-native = ""

LDGOLD_class-native = ""
LDGOLD_class-crosssdk = ""
LDGOLD_libc-glibc_mipsarch = ""
LDGOLD ?= "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', '--enable-gold=default --enable-threads', '--enable-gold --enable-ld=default --enable-threads', d)}"


# This is necessary due to a bug in the binutils Makefiles
# EXTRA_OEMAKE = "configure-build-libiberty all"

export AR = "${HOST_PREFIX}ar"
export AS = "${HOST_PREFIX}as"
export LD = "${HOST_PREFIX}ld"
export NM = "${HOST_PREFIX}nm"
export RANLIB = "${HOST_PREFIX}ranlib"
export OBJCOPY = "${HOST_PREFIX}objcopy"
export OBJDUMP = "${HOST_PREFIX}objdump"

export AR_FOR_TARGET = "${TARGET_PREFIX}ar"
export AS_FOR_TARGET = "${TARGET_PREFIX}as"
export LD_FOR_TARGET = "${TARGET_PREFIX}ld"
export NM_FOR_TARGET = "${TARGET_PREFIX}nm"
export RANLIB_FOR_TARGET = "${TARGET_PREFIX}ranlib"

export CC_FOR_HOST = "${CCACHE}${HOST_PREFIX}gcc ${HOST_CC_ARCH}"
export CXX_FOR_HOST = "${CCACHE}${HOST_PREFIX}gcc ${HOST_CC_ARCH}"

# autotools.bbclass sets the _FOR_BUILD variables, but for some reason we need
# to unset LD_LIBRARY_PATH.
export CC_FOR_BUILD = "LD_LIBRARY_PATH= ${BUILD_CC}"

MULTIARCH := "${@bb.utils.contains("DISTRO_FEATURES", "multiarch", "yes", "no", d)}"
do_configure[vardeps] += "MULTIARCH"
do_configure () {
	(cd ${S} && gnu-configize)

	oe_runconf
#
# must prime config.cache to ensure the build of libiberty
#
	mkdir -p ${B}/build-${BUILD_SYS}
	for i in ${CONFIG_SITE}; do
		cat $i >> ${B}/build-${BUILD_SYS}/config.cache || true
	done
}

do_install () {
	autotools_do_install

	# We don't really need these, so we'll remove them...
	rm -rf ${D}${libdir}/ldscripts

	bindir_rel=${@os.path.relpath('${bindir}', '${prefix}/${TARGET_SYS}/bin')}

	# Fix the /usr/${TARGET_SYS}/bin/* links
	for l in ${D}${prefix}/${TARGET_SYS}/bin/*; do
		rm -f $l
		ln -sf $bindir_rel/${TARGET_PREFIX}`basename $l` $l
	done

	# Install the libiberty header
	install -d ${D}${includedir}
	install -m 644 ${S}/include/ansidecl.h ${D}${includedir}
	install -m 644 ${S}/include/libiberty.h ${D}${includedir}

	# insall pic version of libiberty if available
        if [ -e ${B}/libiberty/pic/libiberty.a ]; then
		install -Dm 0644 ${B}/libiberty/pic/libiberty.a ${D}${libdir}/libiberty.a
	fi

	cd ${D}${bindir}

	# Symlinks for ease of running these on the native target
	for p in ${TARGET_PREFIX}* ; do
		ln -sf $p `echo $p | sed -e s,${TARGET_PREFIX},,`
	done

	for alt in ${USE_ALTERNATIVES_FOR}; do
		rm -f ${D}${bindir}/$alt
	done

	oe_multilib_header bfd.h
        # bfd_stdint.h encodes the compiler name in the header
        sed -i ${D}${includedir}/bfd_stdint.h -e "s,${TARGET_PREFIX},,"
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN}_class-target = "${USE_ALTERNATIVES_FOR}"

python () {
    if bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', True, False, d) and bb.utils.contains_any('TARGET_ARCH', 'riscv32 riscv64', True, False, d):
        bb.fatal("Gold linker does not _yet_ support RISC-V architecture please remove ld-is-gold from DISTRO_FEATURES")
}
