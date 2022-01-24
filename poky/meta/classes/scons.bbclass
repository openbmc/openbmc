inherit python3native

DEPENDS += "python3-scons-native"

EXTRA_OESCONS ?= ""

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

EXPORT_FUNCTIONS do_compile do_install
