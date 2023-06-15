#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

inherit python3native

DEPENDS += "python3-scons-native"

EXTRA_OESCONS ?= ""
# This value below is derived from $(getconf ARG_MAX)
SCONS_MAXLINELENGTH ?= "MAXLINELENGTH=2097152"
EXTRA_OESCONS:append = " ${SCONS_MAXLINELENGTH}"
do_configure() {
	if [ -n "${CONFIGURESTAMPFILE}" -a "${S}" = "${B}" ]; then
		if [ -e "${CONFIGURESTAMPFILE}" -a "`cat ${CONFIGURESTAMPFILE}`" != "${BB_TASKHASH}" -a "${CLEANBROKEN}" != "1" ]; then
			${STAGING_BINDIR_NATIVE}/scons --directory=${S} --clean PREFIX=${prefix} prefix=${prefix} ${EXTRA_OESCONS}
		fi

		mkdir -p `dirname ${CONFIGURESTAMPFILE}`
		echo ${BB_TASKHASH} > ${CONFIGURESTAMPFILE}
	fi
}

scons_do_compile() {
	${STAGING_BINDIR_NATIVE}/scons --directory=${S} ${PARALLEL_MAKE} PREFIX=${prefix} prefix=${prefix} ${EXTRA_OESCONS} || \
	die "scons build execution failed."
}

scons_do_install() {
	${STAGING_BINDIR_NATIVE}/scons --directory=${S} install_root=${D}${prefix} PREFIX=${prefix} prefix=${prefix} ${EXTRA_OESCONS} install || \
	die "scons install execution failed."
}

do_configure[vardepsexclude] = "SCONS_MAXLINELENGTH"
do_compile[vardepsexclude] = "SCONS_MAXLINELENGTH"
do_install[vardepsexclude] = "SCONS_MAXLINELENGTH"

EXPORT_FUNCTIONS do_compile do_install
