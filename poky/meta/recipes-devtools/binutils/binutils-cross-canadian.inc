inherit cross-canadian

SUMMARY = "GNU binary utilities (cross-canadian for ${TARGET_ARCH} target)"
PN = "binutils-cross-canadian-${TRANSLATED_TARGET_ARCH}"
BPN = "binutils"

DEPENDS = "flex-native bison-native virtual/${HOST_PREFIX}gcc-crosssdk virtual/nativesdk-libc nativesdk-zlib nativesdk-gettext nativesdk-flex"
EXTRA_OECONF += "--with-sysroot=${SDKPATH}/sysroots/${TUNE_PKGARCH}${TARGET_VENDOR}-${TARGET_OS} \
                --enable-poison-system-directories \
                "

# We have to point binutils at a sysroot but we don't need to rebuild if this changes
# e.g. we switch between different machines with different tunes.
EXTRA_OECONF[vardepsexclude] = "TUNE_PKGARCH"

do_install () {
	autotools_do_install

	# We're not interested in the libs or headers, these would come from the 
	# nativesdk or target version of the binutils recipe
	rm -rf ${D}${prefix}/${TARGET_SYS}
	rm -f ${D}${libdir}/libbfd*
	rm -f ${D}${libdir}/libiberty*
	rm -f ${D}${libdir}/libopcodes*
	rm -f ${D}${includedir}/*.h
	
	cross_canadian_bindirlinks
}

BBCLASSEXTEND = ""
