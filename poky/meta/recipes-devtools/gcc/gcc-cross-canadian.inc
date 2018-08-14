inherit cross-canadian

SUMMARY = "GNU cc and gcc C compilers (cross-canadian for ${TARGET_ARCH} target)"
PN = "gcc-cross-canadian-${TRANSLATED_TARGET_ARCH}"

DEPENDS = "virtual/${TARGET_PREFIX}gcc virtual/${HOST_PREFIX}gcc-crosssdk virtual/${HOST_PREFIX}binutils-crosssdk virtual/nativesdk-${HOST_PREFIX}libc-for-gcc nativesdk-gettext flex-native"

GCCMULTILIB = "--enable-multilib"

require gcc-configure-common.inc

EXTRA_OECONF_PATHS = "\
    --with-gxx-include-dir=/not/exist${target_includedir}/c++/${BINV} \
    --with-build-time-tools=${STAGING_DIR_NATIVE}${prefix_native}/${TARGET_SYS}/bin \
    --with-sysroot=/not/exist \
    --with-build-sysroot=${STAGING_DIR_TARGET} \
"
# We have to point gcc at a sysroot but we don't need to rebuild if this changes
# e.g. we switch between different machines with different tunes.
EXTRA_OECONF_PATHS[vardepsexclude] = "TUNE_PKGARCH"
TARGET_ARCH[vardepsexclude] = "TUNE_ARCH"
get_gcc_float_setting[vardepvalue] = ""

#
# gcc-cross looks and finds these in ${exec_prefix} but we're not so lucky
# for the sdk. Hardcoding the paths ensures the build doesn't go canadian or worse.
#
export AR_FOR_TARGET = "${TARGET_PREFIX}ar"
export AS_FOR_TARGET = "${TARGET_PREFIX}as"
export DLLTOOL_FOR_TARGET = "${TARGET_PREFIX}dlltool"
export CC_FOR_TARGET = "${TARGET_PREFIX}gcc"
export CXX_FOR_TARGET = "${TARGET_PREFIX}g++"
export GCC_FOR_TARGET = "${TARGET_PREFIX}gcc"
export LD_FOR_TARGET = "${TARGET_PREFIX}ld"
export LIPO_FOR_TARGET = "${TARGET_PREFIX}lipo"
export NM_FOR_TARGET = "${TARGET_PREFIX}nm"
export OBJDUMP_FOR_TARGET = "${TARGET_PREFIX}objdump"
export RANLIB_FOR_TARGET = "${TARGET_PREFIX}ranlib"
export STRIP_FOR_TARGET = "${TARGET_PREFIX}strip"
export WINDRES_FOR_TARGET = "${TARGET_PREFIX}windres"

#
# We need to override this and make sure the compiler can find staging
#
export ARCH_FLAGS_FOR_TARGET = "--sysroot=${STAGING_DIR_TARGET}"

do_configure () {
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

do_compile () {
	oe_runmake all-host configure-target-libgcc
	(cd ${B}/${TARGET_SYS}/libgcc; oe_runmake enable-execute-stack.c unwind.h md-unwind-support.h sfp-machine.h gthr-default.h)
}

# Having anything auto depending on gcc-cross-sdk is a really bad idea...
EXCLUDE_FROM_SHLIBS = "1"

PACKAGES = "${PN}-dbg ${PN} ${PN}-doc"

FILES_${PN} = "\
    ${exec_prefix}/bin/* \
    ${libexecdir}/gcc/${TARGET_SYS}/${BINV}/* \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/*.o \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/specs \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/lib* \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/include \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/include-fixed \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/plugin/include/ \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/plugin/gtype.* \
    ${includedir}/c++/${BINV} \
    ${prefix}/${TARGET_SYS}/bin/* \
    ${prefix}/${TARGET_SYS}/lib/* \
    ${prefix}/${TARGET_SYS}${target_includedir}/* \
"
INSANE_SKIP_${PN} += "dev-so"

FILES_${PN}-doc = "\
    ${infodir} \
    ${mandir} \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/include/README \
"

EXEEXT = ""

# Compute how to get from libexecdir to bindir in python (easier than shell)
BINRELPATH = "${@os.path.relpath(d.expand("${bindir}"), d.expand("${libexecdir}/gcc/${TARGET_SYS}/${BINV}"))}"

do_install () {
	( cd ${B}/${TARGET_SYS}/libgcc; oe_runmake 'DESTDIR=${D}' install-unwind_h-forbuild install-unwind_h )
	oe_runmake 'DESTDIR=${D}' install-host

	# Cleanup some of the ${libdir}{,exec}/gcc stuff ...
	rm -r ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/install-tools
	rm -r ${D}${libexecdir}/gcc/${TARGET_SYS}/${BINV}/install-tools
	rm -rf ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/finclude

	# We care about g++ not c++
	rm -f ${D}${bindir}/*c++

	# We don't care about the gcc-<version> copies
	rm -f ${D}${bindir}/*gcc-?.?*

	# We use libiberty from binutils
	rm -f ${D}${prefix}/${TARGET_SYS}/lib/libiberty.a
	# Not sure where the strange paths come from
	rm -f ${D}${libdir}/../lib/libiberty.a
	rm -f ${D}${libdir}/libiberty.a

	# Cleanup empty directories which are not shipped
	# we use rmdir instead of 'rm -f' to ensure the non empty directories are not deleted
	# ${D}${libdir}/../lib only seems to appear with SDKMACHINE=i686
	local empty_dirs="${D}${libdir}/../lib ${D}${prefix}/${TARGET_SYS}/lib ${D}${prefix}/${TARGET_SYS} ${D}${includedir}"
	for i in $empty_dirs; do
		[ -d $i ] && rmdir --ignore-fail-on-non-empty $i
	done

	# Insert symlinks into libexec so when tools without a prefix are searched for, the correct ones are
	# found.
	dest=${D}${libexecdir}/gcc/${TARGET_SYS}/${BINV}/
	install -d $dest
	suffix=${EXEEXT}
	for t in ar as ld nm objcopy objdump ranlib strip g77 gcc cpp gfortran; do
		if [ "$t" = "g77" -o "$t" = "gfortran" ] && [ ! -e ${D}${bindir}/${TARGET_PREFIX}$t$suffix ]; then
			continue
		fi

		ln -sf ${BINRELPATH}/${TARGET_PREFIX}$t$suffix $dest$t$suffix
	done
	t=real-ld
	ln -sf ${BINRELPATH}/${TARGET_PREFIX}ld$suffix $dest$t$suffix

	# libquadmath headers need to  be available in the gcc libexec dir
	install -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include/
	cp ${S}/libquadmath/quadmath.h ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include/
	cp ${S}/libquadmath/quadmath_weak.h ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include/

	chown -R root:root ${D}
	
	cross_canadian_bindirlinks
}

ELFUTILS = "nativesdk-elfutils"
DEPENDS += "nativesdk-gmp nativesdk-mpfr nativesdk-libmpc ${ELFUTILS} nativesdk-zlib"
RDEPENDS_${PN} += "nativesdk-mpfr nativesdk-libmpc ${ELFUTILS}"

SYSTEMHEADERS = "${target_includedir}/"
SYSTEMLIBS = "${target_base_libdir}/"
SYSTEMLIBS1 = "${target_libdir}/"

EXTRA_OECONF += "--enable-poison-system-directories"

EXTRA_OECONF += "\
    --with-mpfr=${STAGING_DIR_HOST}${layout_exec_prefix} \
    --with-mpc=${STAGING_DIR_HOST}${layout_exec_prefix} \
"

EXTRA_OECONF_append_libc-baremetal = " --without-headers"
EXTRA_OECONF_remove_libc-baremetal = "--with-sysroot=/not/exist"
EXTRA_OECONF_remove_libc-baremetal = "--with-build-sysroot=${STAGING_DIR_TARGET}"

# gcc 4.7 needs -isystem
export ARCH_FLAGS_FOR_TARGET = "--sysroot=${STAGING_DIR_TARGET} -isystem=${target_includedir}"
