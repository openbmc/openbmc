SUMMARY = "Standard GNU compressor"
DESCRIPTION = "GNU Gzip is a popular data compression program originally written by Jean-loup Gailly for the GNU \
project. Mark Adler wrote the decompression part"
HOMEPAGE = "http://www.gnu.org/software/gzip/"
SECTION = "console/utils"
# change to GPLv3+ in 2007/07. Previous GPLv2 version is 1.3.12

inherit autotools texinfo
export DEFS="NO_ASM"

EXTRA_OEMAKE_class-target = "GREP=${base_bindir}/grep"
EXTRA_OECONF_append_libc-musl = " gl_cv_func_fflush_stdin=yes "

do_install_append () {
	if [ "${base_bindir}" != "${bindir}" ]; then
		# Rename and move files into /bin (FHS), which is typical place for gzip
		install -d ${D}${base_bindir}
		mv ${D}${bindir}/gunzip ${D}${base_bindir}/gunzip
		mv ${D}${bindir}/gzip ${D}${base_bindir}/gzip
		mv ${D}${bindir}/zcat ${D}${base_bindir}/zcat
		mv ${D}${bindir}/uncompress ${D}${base_bindir}/uncompress
	fi
}

do_install_append_libc-musl () {
	rm ${D}${libdir}/charset.alias
	rmdir ${D}${libdir}
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_${PN} = "gunzip gzip zcat"
ALTERNATIVE_LINK_NAME[gunzip] = "${base_bindir}/gunzip"
ALTERNATIVE_LINK_NAME[gzip] = "${base_bindir}/gzip"
ALTERNATIVE_LINK_NAME[zcat] = "${base_bindir}/zcat"

export CONFIG_SHELL="/bin/sh"
