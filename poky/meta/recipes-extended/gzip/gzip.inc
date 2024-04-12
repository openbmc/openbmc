SUMMARY = "Standard GNU compressor"
DESCRIPTION = "GNU Gzip is a popular data compression program originally written by Jean-loup Gailly for the GNU \
project. Mark Adler wrote the decompression part"
HOMEPAGE = "http://www.gnu.org/software/gzip/"
SECTION = "console/utils"

inherit autotools texinfo
export DEFS="NO_ASM"

EXTRA_OEMAKE:class-target = "GREP=${base_bindir}/grep"
EXTRA_OEMAKE:append:class-nativesdk = " GREP=grep"
EXTRA_OECONF:append:libc-musl = " gl_cv_func_fflush_stdin=yes "

do_install:append () {
	if [ "${base_bindir}" != "${bindir}" ]; then
		# Rename and move files into /bin (FHS), which is typical place for gzip
		install -d ${D}${base_bindir}
		mv ${D}${bindir}/gunzip ${D}${base_bindir}/gunzip
		mv ${D}${bindir}/gzip ${D}${base_bindir}/gzip
		mv ${D}${bindir}/zcat ${D}${base_bindir}/zcat
		mv ${D}${bindir}/uncompress ${D}${base_bindir}/uncompress
	fi
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE:${PN} = "gunzip gzip zcat"
ALTERNATIVE_LINK_NAME[gunzip] = "${base_bindir}/gunzip"
ALTERNATIVE_LINK_NAME[gzip] = "${base_bindir}/gzip"
ALTERNATIVE_LINK_NAME[zcat] = "${base_bindir}/zcat"

export CONFIG_SHELL="/bin/sh"
