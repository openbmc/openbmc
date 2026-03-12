inherit cross-canadian

SUMMARY = "GNU binary utilities (cross-canadian for ${TARGET_ARCH} target)"
PN = "binutils-cross-canadian-${TRANSLATED_TARGET_ARCH}"
BPN = "binutils"

DEPENDS = "flex-native bison-native virtual/nativesdk-cross-cc virtual/nativesdk-libc nativesdk-zlib nativesdk-gettext nativesdk-flex"
EXTRA_OECONF += "--with-sysroot=${SDKPATH}/sysroots/${TUNE_PKGARCH}${TARGET_VENDOR}-${TARGET_OS} \
                --enable-poison-system-directories \
                "

# We have to point binutils at a sysroot but we don't need to rebuild if this changes
# e.g. we switch between different machines with different tunes.
EXTRA_OECONF[vardepsexclude] = "TUNE_PKGARCH"

# lld complains
# aarch64-yoesdk-linux-ld.lld: error: version script assignment of 'GLIBC_2.0' to symbol 'dlclose' failed: symbol not defined

LDFLAGS:append:toolchain-clang = " -fuse-ld=bfd"

do_install () {
	autotools_do_install

	# We're not interested in the plugins, libs, or headers, these would come from the
	# nativesdk or target version of the binutils recipe
	rm -rf ${D}${prefix}/${TARGET_SYS}
	rm -rf ${D}${libdir}/bfd-plugins
	rm -f ${D}${libdir}/libbfd*
	rm -f ${D}${libdir}/libiberty*
	rm -f ${D}${libdir}/libopcodes*
	rm -f ${D}${includedir}/*.h
	rm -f ${D}${sysconfdir}/gprofng.rc
	rmdir ${D}${sysconfdir} || :

	cross_canadian_bindirlinks
}

BBCLASSEXTEND = ""
