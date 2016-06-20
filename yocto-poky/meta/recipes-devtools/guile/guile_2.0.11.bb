SUMMARY = "Guile is the GNU Ubiquitous Intelligent Language for Extensions"
DESCRIPTION = "Guile is the GNU Ubiquitous Intelligent Language for Extensions,\
 the official extension language for the GNU operating system.\
 Guile is a library designed to help programmers create flexible applications.\
 Using Guile in an application allows the application's functionality to be\
 extended by users or other programmers with plug-ins, modules, or scripts.\
 Guile provides what might be described as 'practical software freedom,'\
 making it possible for users to customize an application to meet their\
 needs without digging into the application's internals."

HOMEPAGE = "http://www.gnu.org/software/guile/"
SECTION = "devel"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/guile/guile-${PV}.tar.xz \
           file://debian/0002-Mark-Unused-modules-are-removed-gc-test-as-unresolve.patch \
           file://debian/0003-Mark-mutex-with-owner-not-retained-threads-test-as-u.patch \
           file://opensuse/guile-64bit.patch \
           file://guile_2.0.6_fix_sed_error.patch \
           file://arm_endianness.patch \
           file://arm_aarch64.patch \
           file://workaround-ice-ssa-corruption.patch \
           file://libguile-Makefile.am-hook.patch \
           file://libguile-VM-ASM_MUL-for-ARM-Add-earlyclobber.patch \
           file://remove_strcase_l_funcs.patch \
           file://0001-libguile-Check-for-strtol_l-during-configure.patch \
           file://0002-Recognize-nios2-as-compilation-target.patch \
           "

#           file://debian/0001-Change-guile-to-guile-X.Y-for-info-pages.patch
#           file://opensuse/guile-turn-off-gc-test.patch

SRC_URI[md5sum] = "03f1bce1a4983076d955003472306a13"
SRC_URI[sha256sum] = "aed0a4a6db4e310cbdfeb3613fa6f86fddc91ef624c1e3f8937a6304c69103e2"


inherit autotools gettext pkgconfig texinfo
BBCLASSEXTEND = "native"

DEPENDS = "libunistring bdwgc gmp libtool libffi ncurses readline"
# add guile-native only to the target recipe's DEPENDS
DEPENDS_append_class-target = " guile-native libatomic-ops"

# The comment of the script guile-config said it has been deprecated but we should
# at least add the required dependency to make it work since we still provide the script.
RDEPENDS_${PN} = "pkgconfig"

RDEPENDS_${PN}_append_libc-glibc_class-target = " glibc-gconv-iso8859-1"

EXTRA_OECONF += "${@['--without-libltdl-prefix --without-libgmp-prefix --without-libreadline-prefix', ''][bb.data.inherits_class('native',d)]}"

EXTRA_OECONF_append_class-target = " --with-libunistring-prefix=${STAGING_LIBDIR} \
                                     --with-libgmp-prefix=${STAGING_LIBDIR} \
                                     --with-libltdl-prefix=${STAGING_LIBDIR}"
EXTRA_OECONF_append_libc-uclibc = " guile_cv_use_csqrt=no "

CFLAGS_append_libc-musl = " -DHAVE_GC_SET_FINALIZER_NOTIFIER \
	                    -DHAVE_GC_GET_HEAP_USAGE_SAFE \
	                    -DHAVE_GC_GET_FREE_SPACE_DIVISOR \
	                    -DHAVE_GC_SET_FINALIZE_ON_DEMAND \
                           "

do_configure_prepend() {
	mkdir -p po
}

export GUILE_FOR_BUILD="${BUILD_SYS}-guile"

do_install_append_class-native() {
	install -m 0755  ${D}${bindir}/guile ${D}${bindir}/${HOST_SYS}-guile

	create_wrapper ${D}/${bindir}/guile \
		GUILE_LOAD_PATH=${STAGING_DATADIR_NATIVE}/guile/2.0 \
		GUILE_LOAD_COMPILED_PATH=${STAGING_LIBDIR_NATIVE}/guile/2.0/ccache
	create_wrapper ${D}${bindir}/${HOST_SYS}-guile \
		GUILE_LOAD_PATH=${STAGING_DATADIR_NATIVE}/guile/2.0 \
		GUILE_LOAD_COMPILED_PATH=${STAGING_LIBDIR_NATIVE}/guile/2.0/ccache
}

do_install_append_class-target() {
	# cleanup buildpaths in scripts
	sed -i -e 's:${STAGING_DIR_NATIVE}::' ${D}${bindir}/guile-config
	sed -i -e 's:${STAGING_DIR_HOST}::' ${D}${bindir}/guile-snarf

	sed -i -e 's:${STAGING_DIR_TARGET}::g' ${D}${libdir}/pkgconfig/guile-2.0.pc
}

do_install_append_libc-musl() {
	rm -f ${D}${libdir}/charset.alias
}

SYSROOT_PREPROCESS_FUNCS = "guile_cross_config"

guile_cross_config() {
	# this is only for target recipe
	if [ "${PN}" = "guile" ]
	then
	        # Create guile-config returning target values instead of native values
	        install -d ${SYSROOT_DESTDIR}${STAGING_BINDIR_CROSS}
        	echo '#!'`which ${BUILD_SYS}-guile`$' \\\n--no-auto-compile -e main -s\n!#\n(define %guile-build-info '\'\( \
			> ${B}/guile-config.cross
	        sed -n -e 's:^[ \t]*{[ \t]*":  (:' \
			-e 's:",[ \t]*": . ":' \
			-e 's:" *}, *\\:"):' \
			-e 's:^.*cachedir.*$::' \
			-e '/^  (/p' \
			< ${B}/libguile/libpath.h >> ${B}/guile-config.cross
	        echo '))' >> ${B}/guile-config.cross
	        cat ${B}/meta/guile-config >> ${B}/guile-config.cross
	        install ${B}/guile-config.cross ${STAGING_BINDIR_CROSS}/guile-config
	fi
}

# Guile needs the compiled files to be newer than the source, and it won't
# auto-compile into the prefix even if it can write there, so touch them here as
# sysroot is managed.
SSTATEPOSTINSTFUNCS += "guile_sstate_postinst"
guile_sstate_postinst() {
	if [ "${BB_CURRENTTASK}" = "populate_sysroot" -o "${BB_CURRENTTASK}" = "populate_sysroot_setscene" ]
	then
                find ${STAGING_DIR_TARGET}/${libdir}/guile/2.0/ccache -type f | xargs touch
	fi
}

# http://errors.yoctoproject.org/Errors/Details/20491/
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
