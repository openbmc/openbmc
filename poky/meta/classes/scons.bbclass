inherit python3native

DEPENDS += "python3-scons-native"

EXTRA_OESCONS ?= ""

do_configure() {
	unset _PYTHON_SYSCONFIGDATA_NAME
	if [ -n "${CONFIGURESTAMPFILE}" ]; then
		if [ -e "${CONFIGURESTAMPFILE}" -a "`cat ${CONFIGURESTAMPFILE}`" != "${BB_TASKHASH}" -a "${CLEANBROKEN}" != "1" ]; then
			${STAGING_BINDIR_NATIVE}/scons --clean PREFIX=${prefix} prefix=${prefix} ${EXTRA_OESCONS}
		fi

		mkdir -p `dirname ${CONFIGURESTAMPFILE}`
		echo ${BB_TASKHASH} > ${CONFIGURESTAMPFILE}
	fi
}

scons_do_compile() {
	unset _PYTHON_SYSCONFIGDATA_NAME
	${STAGING_BINDIR_NATIVE}/scons ${PARALLEL_MAKE} PREFIX=${prefix} prefix=${prefix} ${EXTRA_OESCONS} || \
	die "scons build execution failed."
}

scons_do_install() {
	unset _PYTHON_SYSCONFIGDATA_NAME
	${STAGING_BINDIR_NATIVE}/scons install_root=${D}${prefix} PREFIX=${prefix} prefix=${prefix} ${EXTRA_OESCONS} install || \
	die "scons install execution failed."
}

EXPORT_FUNCTIONS do_compile do_install
