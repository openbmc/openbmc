require glibc-collateral.inc

SUMMARY = "Locale data from glibc"

BPN = "glibc"
LOCALEBASEPN = "${MLPREFIX}glibc"

# Do not inhibit default deps, do_package requires binutils/gcc for
# objcopy/gcc-nm and glibc-locale depends on virtual/libc directly.
INHIBIT_DEFAULT_DEPS = ""

# Binary locales are generated at build time if ENABLE_BINARY_LOCALE_GENERATION
# is set. The idea is to avoid running localedef on the target (at first boot)
# to decrease initial boot time and avoid localedef being killed by the OOM
# killer which used to effectively break i18n on machines with < 128MB RAM.

# default to disabled
ENABLE_BINARY_LOCALE_GENERATION ?= "0"
ENABLE_BINARY_LOCALE_GENERATION:pn-nativesdk-glibc-locale = "1"

#enable locale generation on these arches
# BINARY_LOCALE_ARCHES is a space separated list of regular expressions
BINARY_LOCALE_ARCHES ?= "arc arm.* aarch64 i[3-6]86 x86_64 powerpc mips mips64 riscv32 riscv64 loongarch64"

# set "1" to use cross-localedef for locale generation
# set "0" for qemu emulation of native localedef for locale generation
LOCALE_GENERATION_WITH_CROSS-LOCALEDEF = "1"

PROVIDES = "virtual/libc-locale"

PACKAGES = "localedef ${PN}-dbg ${LOCALEBASEPN}-locale-alias"

PACKAGES_DYNAMIC = "^locale-base-.* \
                    ^glibc-gconv-.* ^glibc-charmap-.* ^glibc-localedata-.* ^glibc-binary-localedata-.* \
                    ^${MLPREFIX}glibc-gconv$"

# Create a glibc-binaries package
ALLOW_EMPTY:${BPN}-binaries = "1"
PACKAGES += "${BPN}-binaries"
RRECOMMENDS:${BPN}-binaries =  "${@" ".join([p for p in d.getVar('PACKAGES').split() if p.find("glibc-binary-") != -1])}"

# Create a glibc-charmaps package
ALLOW_EMPTY:${BPN}-charmaps = "1"
PACKAGES += "${BPN}-charmaps"
RRECOMMENDS:${BPN}-charmaps =  "${@" ".join([p for p in d.getVar('PACKAGES').split() if p.find("glibc-charmap-") != -1])}"

# Create a glibc-gconvs package
ALLOW_EMPTY:${BPN}-gconvs = "1"
PACKAGES += "${BPN}-gconvs"
RRECOMMENDS:${BPN}-gconvs =  "${@" ".join([p for p in d.getVar('PACKAGES').split() if p.find("glibc-gconv-") != -1])}"

# Create a glibc-localedatas package
ALLOW_EMPTY:${BPN}-localedatas = "1"
PACKAGES += "${BPN}-localedatas"
RRECOMMENDS:${BPN}-localedatas =  "${@" ".join([p for p in d.getVar('PACKAGES').split() if p.find("glibc-localedata-") != -1])}"

DESCRIPTION:localedef = "glibc: compile locale definition files"

# glibc-gconv is dynamically added into PACKAGES, thus
# FILES:glibc-gconv will not be automatically extended in multilib.
# Explicitly add ${MLPREFIX} for FILES:glibc-gconv.
FILES:${MLPREFIX}glibc-gconv = "${libdir}/gconv/*"
FILES:localedef = "${bindir}/localedef"
FILES:${LOCALEBASEPN}-locale-alias = "${datadir}/locale/locale.alias"

LOCALETREESRC = "${COMPONENTS_DIR}/${PACKAGE_ARCH}/glibc-stash-locale"

copy_locale_files() {
	local dir=$1 mode=$2

	[ -e "${LOCALETREESRC}$dir" ] || return 0

	for d in . $(find "${LOCALETREESRC}$dir" -type d -printf '%P '); do
		install -d ${D}$dir/$d
		find "${LOCALETREESRC}$dir/$d" -maxdepth 1 -type f \
			-exec install -m $mode -t "${D}$dir/$d" {} \;
	done
}

do_install() {
	copy_locale_files ${bindir} 0755
	copy_locale_files ${localedir} 0644
	if [ ${PACKAGE_NO_GCONV} -eq 0 ]; then
		copy_locale_files ${libdir}/gconv 0755
		copy_locale_files ${datadir}/i18n 0644
	fi
	# Remove empty dirs in libdir when gconv or locales are not copied
	find ${D}${libdir} -type d -empty -delete
	copy_locale_files ${datadir}/locale 0644
	install -m 0644 ${LOCALETREESRC}/SUPPORTED ${WORKDIR}/SUPPORTED
}

inherit libc-package

BBCLASSEXTEND = "nativesdk"

# Don't scan for CVEs as glibc will be scanned
CVE_PRODUCT = ""
