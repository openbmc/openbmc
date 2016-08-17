# The DTDs of the various versions have to be installed in parallel and
# should not replace each other. The installation step is common for
# all versions and just differs in ${DTD_VERSION} which is set in each
# version recipe.
#
# The DTDs do have some quirks (see LFS documentation).
SUMMARY = "Document type definitions for verification of SGML data files"
DESCRIPTION = "Document type definitions for verification of SGML data \
files against the DocBook rule set"
HOMEPAGE = "http://www.docbook.org/sgml/"

DEPENDS = "sgml-common-native"

# Note: the upstream sources are not distributed with a license file.
# LICENSE-OASIS is included as a "patch" to workaround this. When
# upgrading this recipe, please verify whether this is still needed.
SRC_URI = "http://www.docbook.org/sgml/${DTD_VERSION}/docbook-${DTD_VERSION}.zip \
           file://LICENSE-OASIS"

# The .zip file extracts to the current dir
S = "${WORKDIR}"

INC_PR = "r3"

SSTATEPOSTINSTFUNCS += "docbook_sgml_dtd_sstate_postinst"
SYSROOT_PREPROCESS_FUNCS += "docbook_sgml_dtd_sysroot_preprocess"

inherit native

do_install () {
	# Refer to http://www.linuxfromscratch.org/blfs/view/stable/pst/sgml-dtd.html
	# for details.
	install -d -m 755 ${D}${datadir}/sgml/docbook/sgml-dtd-${DTD_VERSION}
	install docbook.cat ${D}${datadir}/sgml/docbook/sgml-dtd-${DTD_VERSION}/catalog
	cp -PpRr *.dtd *.mod *.dcl ${D}${datadir}/sgml/docbook/sgml-dtd-${DTD_VERSION}

	install -d ${D}${sysconfdir}/sgml
	echo "CATALOG ${datadir}/sgml/docbook/sgml-dtd-${DTD_VERSION}/catalog" > \
		${D}${sysconfdir}/sgml/sgml-docbook-dtd-${DTD_VERSION}.cat
}

docbook_sgml_dtd_sstate_postinst () {
	if [ "${BB_CURRENTTASK}" = "populate_sysroot" -o "${BB_CURRENTTASK}" = "populate_sysroot_setscene" ]
	then
		# Ensure that the catalog file sgml-docbook.cat is properly
		# updated when the package is installed from sstate cache.
		${SYSROOT_DESTDIR}${bindir_crossscripts}/install-catalog-docbook-sgml-dtd-${DTD_VERSION} \
			--add ${sysconfdir}/sgml/sgml-docbook.bak \
			${sysconfdir}/sgml/sgml-docbook-dtd-${DTD_VERSION}.cat
		${SYSROOT_DESTDIR}${bindir_crossscripts}/install-catalog-docbook-sgml-dtd-${DTD_VERSION} \
			--add ${sysconfdir}/sgml/sgml-docbook.cat \
			${sysconfdir}/sgml/sgml-docbook-dtd-${DTD_VERSION}.cat
	fi
}

docbook_sgml_dtd_sysroot_preprocess () {
    install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
    install -m 755 ${STAGING_BINDIR_NATIVE}/install-catalog ${SYSROOT_DESTDIR}${bindir_crossscripts}/install-catalog-docbook-sgml-dtd-${DTD_VERSION}
}

CLEANFUNCS += "docbook_sgml_dtd_sstate_clean"

docbook_sgml_dtd_sstate_clean () {
	# Ensure that the catalog file sgml-docbook.cat is properly
	# updated when the package is removed from sstate cache.
	files="${sysconfdir}/sgml/sgml-docbook.bak ${sysconfdir}/sgml/sgml-docbook.cat"
	for f in $files; do
		[ ! -f $f ] || sed -i '/\/sgml\/sgml-docbook-dtd-${DTD_VERSION}.cat/d' $f
	done
}
