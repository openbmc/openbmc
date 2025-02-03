inherit cross-canadian

SUMMARY = "GNU cc and gcc C compilers (cross-canadian for ${TARGET_ARCH} target)"
PN = "gcc-cross-canadian-${TRANSLATED_TARGET_ARCH}"

DEPENDS = "virtual/nativesdk-cross-cc virtual/cross-cc virtual/nativesdk-cross-binutils virtual/nativesdk-libc nativesdk-gettext flex-native virtual/libc"

GCCMULTILIB = "--enable-multilib"

require gcc-configure-common.inc

EXTRA_OECONF += "--with-plugin-ld=ld"
EXTRA_OECONF_PATHS = "\
    --with-gxx-include-dir=/not/exist${target_includedir}/c++/${BINV} \
    --with-gxx-libcxx-include-dir=/not/exist${target_includedir}/c++/v1 \
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
        if [ ! -d ${RECIPE_SYSROOT}/${target_includedir} ]; then
            mkdir -p ${RECIPE_SYSROOT}/${target_includedir}
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

do_compile () {
	remove_sysroot_paths_from_configargs '/host'
	remove_sysroot_paths_from_checksum_options '${STAGING_DIR_HOST}' '/host'

	oe_runmake all-host configure-target-libgcc
	(cd ${B}/${TARGET_SYS}/libgcc; oe_runmake enable-execute-stack.c unwind.h md-unwind-support.h sfp-machine.h gthr-default.h)
}

PACKAGES = "${PN}-dbg ${PN} ${PN}-doc"

FILES:${PN} = "\
    ${exec_prefix}/bin/* \
    ${libexecdir}/gcc/${TARGET_SYS}/${BINV}/* \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/*.o \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/specs \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/lib* \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/include \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/include-fixed \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/plugin/include/ \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/plugin/gtype.* \
    ${libdir}/bfd-plugins/*.so \
    ${includedir}/c++/${BINV} \
    ${prefix}/${TARGET_SYS}/bin/* \
    ${prefix}/${TARGET_SYS}/lib/* \
    ${prefix}/${TARGET_SYS}${target_includedir}/* \
"
INSANE_SKIP:${PN} += "dev-so"

FILES:${PN}-doc = "\
    ${infodir} \
    ${mandir} \
    ${gcclibdir}/${TARGET_SYS}/${BINV}/include/README \
"

EXEEXT = ""

# Compute how to get from libexecdir to bindir in python (easier than shell)
BINRELPATH = "${@os.path.relpath(d.expand("${bindir}"), d.expand("${libexecdir}/gcc/${TARGET_SYS}/${BINV}"))}"
# linker plugin path
LIBRELPATH = "${@os.path.relpath(d.expand("${libexecdir}/gcc/${TARGET_SYS}/${BINV}"), d.expand("${libdir}/bfd-plugins"))}"

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
	rm -f ${D}${bindir}/*gcc-${BINV}*

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
	for t in ar as ld ld.bfd ld.gold nm objcopy objdump ranlib strip g77 gcc cpp gfortran; do
		if [ "$t" = "g77" -o "$t" = "gfortran" ] && [ ! -e ${D}${bindir}/${TARGET_PREFIX}$t$suffix ]; then
			continue
		fi

		ln -sf ${BINRELPATH}/${TARGET_PREFIX}$t$suffix $dest$t$suffix
	done

	# libquadmath headers need to  be available in the gcc libexec dir
	install -d ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include/
	cp ${S}/libquadmath/quadmath.h ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include/
	cp ${S}/libquadmath/quadmath_weak.h ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include/

	# install LTO linker plugins where binutils tools can find it
	install -d ${D}${libdir}/bfd-plugins
	ln -sf ${LIBRELPATH}/liblto_plugin.so ${D}${libdir}/bfd-plugins/liblto_plugin.so

	chown -R root:root ${D}

	cross_canadian_bindirlinks

	for i in linux ${CANADIANEXTRAOS}
	do
		for v in ${CANADIANEXTRAVENDOR}
		do
			d=${D}${bindir}/../${TARGET_ARCH}$v-$i
			install -d $d
			for j in ${TARGET_PREFIX}gcc${EXEEXT} ${TARGET_PREFIX}g++${EXEEXT}
			do
				p=${TARGET_ARCH}$v-$i-`echo $j | sed -e s,${TARGET_PREFIX},,`
				case $i in
				*musl*)
					rm -rf $d/$p
					echo "#!/usr/bin/env sh" > $d/$p
					echo "exec \`dirname \$0\`/../${TARGET_SYS}/$j -mmusl \$@" >> $d/$p
					chmod 0755 $d/$p
					;;
				*)
					;;
				esac
			done
		done
	done

	cleanup_installed_include_fixed
}

ELFUTILS = "nativesdk-elfutils"
DEPENDS += "nativesdk-gmp nativesdk-mpfr nativesdk-libmpc ${ELFUTILS} nativesdk-zlib nativesdk-zstd"
RDEPENDS:${PN} += "nativesdk-mpfr nativesdk-libmpc ${ELFUTILS}"

SYSTEMHEADERS = "${target_includedir}/"
SYSTEMLIBS = "${target_base_libdir}/"
SYSTEMLIBS1 = "${target_libdir}/"

EXTRA_OECONF += "--enable-poison-system-directories"

# gcc 4.7 needs -isystem
export ARCH_FLAGS_FOR_TARGET = "--sysroot=${STAGING_DIR_TARGET} -isystem=${target_includedir}"
