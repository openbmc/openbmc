require glibc-collateral.inc

SUMMARY = "Locale data from glibc"

BPN = "glibc"
LOCALEBASEPN = "${MLPREFIX}glibc"

# glibc-collateral.inc inhibits all default deps, but do_package needs objcopy
# ERROR: objcopy failed with exit code 127 (cmd was 'i586-webos-linux-objcopy' --only-keep-debug 'glibc-locale/2.17-r0/package/usr/lib/gconv/IBM1166.so' 'glibc-locale/2.17-r0/package/usr/lib/gconv/.debug/IBM1166.so')
# ERROR: Function failed: split_and_strip_files
BINUTILSDEP = "virtual/${MLPREFIX}${TARGET_PREFIX}binutils:do_populate_sysroot"
BINUTILSDEP_class-nativesdk = "virtual/${TARGET_PREFIX}binutils-crosssdk:do_populate_sysroot"
do_package[depends] += "${BINUTILSDEP}"

# localedef links with libc.so and glibc-collateral.inc inhibits all default deps
# cannot add virtual/libc to DEPENDS, because it would conflict with libc-initial in RSS
RDEPENDS_localedef += "glibc"

# Binary locales are generated at build time if ENABLE_BINARY_LOCALE_GENERATION
# is set. The idea is to avoid running localedef on the target (at first boot)
# to decrease initial boot time and avoid localedef being killed by the OOM
# killer which used to effectively break i18n on machines with < 128MB RAM.

# default to disabled
ENABLE_BINARY_LOCALE_GENERATION ?= "0"
ENABLE_BINARY_LOCALE_GENERATION_pn-nativesdk-glibc-locale = "1"

#enable locale generation on these arches
# BINARY_LOCALE_ARCHES is a space separated list of regular expressions
BINARY_LOCALE_ARCHES ?= "arm.* aarch64 i[3-6]86 x86_64 powerpc mips mips64 riscv32 riscv64"

# set "1" to use cross-localedef for locale generation
# set "0" for qemu emulation of native localedef for locale generation
LOCALE_GENERATION_WITH_CROSS-LOCALEDEF = "1"

PROVIDES = "virtual/libc-locale"

PACKAGES = "localedef ${PN}-dbg"

PACKAGES_DYNAMIC = "^locale-base-.* \
                    ^glibc-gconv-.* ^glibc-charmap-.* ^glibc-localedata-.* ^glibc-binary-localedata-.* \
                    ^${MLPREFIX}glibc-gconv$"

# Create a glibc-binaries package
ALLOW_EMPTY_${BPN}-binaries = "1"
PACKAGES += "${BPN}-binaries"
RRECOMMENDS_${BPN}-binaries =  "${@" ".join([p for p in d.getVar('PACKAGES').split() if p.find("glibc-binary") != -1])}"

# Create a glibc-charmaps package
ALLOW_EMPTY_${BPN}-charmaps = "1"
PACKAGES += "${BPN}-charmaps"
RRECOMMENDS_${BPN}-charmaps =  "${@" ".join([p for p in d.getVar('PACKAGES').split() if p.find("glibc-charmap") != -1])}"

# Create a glibc-gconvs package
ALLOW_EMPTY_${BPN}-gconvs = "1"
PACKAGES += "${BPN}-gconvs"
RRECOMMENDS_${BPN}-gconvs =  "${@" ".join([p for p in d.getVar('PACKAGES').split() if p.find("glibc-gconv") != -1])}"

# Create a glibc-localedatas package
ALLOW_EMPTY_${BPN}-localedatas = "1"
PACKAGES += "${BPN}-localedatas"
RRECOMMENDS_${BPN}-localedatas =  "${@" ".join([p for p in d.getVar('PACKAGES').split() if p.find("glibc-localedata") != -1])}"

DESCRIPTION_localedef = "glibc: compile locale definition files"

# glibc-gconv is dynamically added into PACKAGES, thus
# FILES_glibc-gconv will not be automatically extended in multilib.
# Explicitly add ${MLPREFIX} for FILES_glibc-gconv.
FILES_${MLPREFIX}glibc-gconv = "${libdir}/gconv/*"
FILES_localedef = "${bindir}/localedef"

LOCALETREESRC = "${COMPONENTS_DIR}/${PACKAGE_ARCH}/glibc-stash-locale"

do_install () {
	mkdir -p ${D}${bindir} ${D}${datadir} ${D}${libdir}
	if [ -n "$(ls ${LOCALETREESRC}/${bindir})" ]; then
		cp -R --no-dereference --preserve=mode,links ${LOCALETREESRC}/${bindir}/* ${D}${bindir}
	fi
	if [ -n "$(ls ${LOCALETREESRC}/${localedir})" ]; then
		mkdir -p ${D}${localedir}
		cp -R --no-dereference --preserve=mode,links ${LOCALETREESRC}/${localedir}/* ${D}${localedir}
	fi
	if [ -e ${LOCALETREESRC}/${libdir}/gconv ]; then
		cp -R --no-dereference --preserve=mode,links ${LOCALETREESRC}/${libdir}/gconv ${D}${libdir}
	fi
	if [ -e ${LOCALETREESRC}/${datadir}/i18n ]; then
		cp -R --no-dereference --preserve=mode,links ${LOCALETREESRC}/${datadir}/i18n ${D}${datadir}
	fi
	if [ -e ${LOCALETREESRC}/${datadir}/locale ]; then
		cp -R --no-dereference --preserve=mode,links ${LOCALETREESRC}/${datadir}/locale ${D}${datadir}
	fi
	cp -R --no-dereference --preserve=mode,links ${LOCALETREESRC}/SUPPORTED ${WORKDIR}
}

inherit libc-package

BBCLASSEXTEND = "nativesdk"
