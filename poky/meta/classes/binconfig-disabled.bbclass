#
# Class to disable binconfig files instead of installing them
#

# The list of scripts which should be disabled.
BINCONFIG ?= ""

FILES:${PN}-dev += "${bindir}/*-config"

do_install:append () {
	for x in ${BINCONFIG}; do
		# Make the disabled script emit invalid parameters for those configure
		# scripts which call it without checking the return code.
		echo "#!/bin/sh" > ${D}$x
		echo "echo 'ERROR: $x should not be used, use an alternative such as pkg-config' >&2" >> ${D}$x
		echo "echo '--should-not-have-used-$x'" >> ${D}$x
		echo "exit 1" >> ${D}$x
		chmod +x ${D}$x
	done
}

SYSROOT_PREPROCESS_FUNCS += "binconfig_disabled_sysroot_preprocess"

binconfig_disabled_sysroot_preprocess () {
	for x in ${BINCONFIG}; do
		configname=`basename $x`
		install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}
		install ${D}$x ${SYSROOT_DESTDIR}${bindir_crossscripts}
	done
}
