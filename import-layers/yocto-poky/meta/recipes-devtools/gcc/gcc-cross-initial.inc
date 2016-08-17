DEPENDS = "virtual/${TARGET_PREFIX}binutils ${NATIVEDEPS}"
PROVIDES = "virtual/${TARGET_PREFIX}gcc-initial"
PACKAGES = ""

INHIBIT_AUTOTOOLS_DEPS = "1"
INHIBIT_DEFAULT_DEPS = "1"

# We still need gnu-config-native
DEPENDS_prepend = "gnu-config-native autoconf-native "

PN = "gcc-cross-initial-${TARGET_ARCH}"

CROSS_TARGET_SYS_DIR_append = ".${PN}"

# This is intended to be a -very- basic config
# sysroot is needed in case we use libc-initial
EXTRA_OECONF = "\
    --with-newlib \
    --without-headers \
    --disable-shared \
    --disable-threads \
    --disable-multilib \
    --disable-__cxa_atexit \
    --enable-languages=c \
    --program-prefix=${TARGET_PREFIX} \
    --with-sysroot=/not/exist \
    --with-build-sysroot=${GCCCROSS_BUILDSYSROOT} \
    ${EXTRA_OECONF_INITIAL} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', '--with-ld=${STAGING_BINDIR_TOOLCHAIN}/${TARGET_PREFIX}ld.bfd', '', d)} \
    ${EXTRA_OECONF_GCC_FLOAT} \
    ${@get_gcc_ppc_plt_settings(bb, d)} \
"

EXTRA_OECONF += "--with-native-system-header-dir=${SYSTEMHEADERS}"

GCCCROSS_BUILDSYSROOT = "${B}/tmpsysroot"

do_configure_prepend () {
	sysr=${GCCCROSS_BUILDSYSROOT}${target_includedir}
	mkdir -p $sysr
	for t in linux asm asm-generic; do
		rm -f $sysr/$t
		ln -s ${STAGING_DIR_TARGET}${target_includedir}/$t $sysr/
	done
}

do_compile () {
    oe_runmake all-gcc configure-target-libgcc
}
do_install () {
	( cd ${B}/${TARGET_SYS}/libgcc; oe_runmake 'DESTDIR=${D}' install-unwind_h )
	oe_runmake 'DESTDIR=${D}' install-gcc

	# We don't really need this (here shares/ contains man/, info/, locale/).
	rm -rf ${D}${datadir}/

	# We use libiberty from binutils
	find ${D}${exec_prefix}/lib -name libiberty.a | xargs rm -f
	find ${D}${exec_prefix}/lib -name libiberty.h | xargs rm -f

	# Insert symlinks into libexec so when tools without a prefix are searched for, the correct ones are
	# found. These need to be relative paths so they work in different locations.
	dest=${D}${libexecdir}/gcc/${TARGET_SYS}/${BINV}/
	install -d $dest
	useld=${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', '.bfd', '', d)}
	ln -sf ${BINRELPATH}/${TARGET_PREFIX}ld${useld} ${dest}ld
	for t in ar as nm objcopy objdump ranlib strip g77 gcc cpp gfortran; do
		ln -sf ${BINRELPATH}/${TARGET_PREFIX}$t $dest$t
	done
	# fixed limits.h infact includes the so called real limits.h
	# which should come from libc but when we build libc-initial
	# then bunch of configure tests include fixed limits.h which in turn
	# includes real limits.h but this real limits.h is not staged yet
	# so we overwirte the generated include-fixed/limits.h for gcc-cross-initial
	# to get rid references to real limits.h
	cp gcc/include-fixed/limits.h ${D}${gcclibdir}/${TARGET_SYS}/${BINV}/include/limits.h

	# gcc-runtime installs libgcc into a special location in staging since it breaks doing a standalone build
	case ${PN} in
		*gcc-cross-initial-${TARGET_ARCH}|*gcc-crosssdk-initial-${TARGET_ARCH})
			dest=${D}/${includedir}/gcc-build-internal-initial-${TARGET_SYS}
			hardlinkdir . $dest
		;;
	esac
}
#
# Override the default sysroot staging copy since this won't look like a target system
#
sysroot_stage_all() {
	sysroot_stage_dir ${D} ${SYSROOT_DESTDIR}
	install -d ${SYSROOT_DESTDIR}${STAGING_DIR_TARGET}${target_base_libdir}/
	install -d ${SYSROOT_DESTDIR}${STAGING_DIR_TARGET}${target_libdir}/
	mv ${SYSROOT_DESTDIR}${target_base_libdir}/* ${SYSROOT_DESTDIR}${STAGING_DIR_TARGET}${target_base_libdir}/ || true
	mv ${SYSROOT_DESTDIR}${target_libdir}/* ${SYSROOT_DESTDIR}${STAGING_DIR_TARGET}${target_libdir}/ || true
}

do_populate_sysroot[sstate-inputdirs] = "${SYSROOT_DESTDIR}/${STAGING_DIR_HOST}/ ${SYSROOT_DESTDIR}/${STAGING_DIR_TARGET}/${target_base_libdir}/"
do_populate_sysroot[sstate-outputdirs] = "${STAGING_DIR_HOST}/ ${STAGING_DIR_TCBOOTSTRAP}/${target_base_libdir}/"

inherit nopackages
