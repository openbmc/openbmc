SUMMARY = "Base utilities for working with SGML and XML"
DESCRIPTION = "The sgml-common package gathers very basic \
stuff necessary to work with SGML and XML, such as xml.dcl, \
a SGML declaration of XML; iso-entities, a list of the basic \
SGML ISO entities; and install-catalog, a script used to \
add entries to (or remove entries from) centralized catalogs \
whose entries are pointers to SGML open catalogs, \
as defined by OASIS."
HOMEPAGE = "http://sources.redhat.com/docbook-tools/"
LICENSE = "GPLv2+"
# See the comments in license.patch when upgrading this recipe.
# This is inteded to be a temporary workaround.
LIC_FILES_CHKSUM = "file://LICENSE-GPLv2;md5=ab8a50abe86dfc859e148baae043c89b"
SECTION = "base"

PR = "r1"

SRC_URI = "ftp://sources.redhat.com/pub/docbook-tools/new-trials/SOURCES/sgml-common-${PV}.tgz \
           file://autohell.patch \
           file://license.patch"

SRC_URI[md5sum] = "103c9828f24820df86e55e7862e28974"
SRC_URI[sha256sum] = "7dc418c1d361123ffc5e45d61f1b97257940a8eb35d0bfbbc493381cc5b1f959"

inherit autotools

do_compile_append() {
	# install-catalog script contains hardcoded references to /etc/sgml
	sed -i -e 's|\([ "]\+\)/etc/sgml|\1${sysconfdir}/sgml|g' bin/install-catalog
}

FILES_${PN} += "${datadir}/sgml"

pkg_postinst_${PN}() {
    if [ "x$D" = "x" ]; then
	install-catalog \
		--add ${sysconfdir}/sgml/sgml-ent.cat \
		${datadir}/sgml/sgml-iso-entities-8879.1986/catalog

	install-catalog \
		--add ${sysconfdir}/sgml/sgml-docbook.cat \
		${sysconfdir}/sgml/sgml-ent.cat
    else
	if ! grep -q ${datadir}/sgml/sgml-iso-entities-8879.1986/catalog $D${sysconfdir}/sgml/sgml-ent.cat; then
	    echo "CATALOG ${datadir}/sgml/sgml-iso-entities-8879.1986/catalog" >> $D${sysconfdir}/sgml/sgml-ent.cat
	fi
	if ! grep -q ${sysconfdir}/sgml/sgml-ent.cat $D${sysconfdir}/sgml/catalog; then
	    echo "CATALOG ${sysconfdir}/sgml/sgml-ent.cat" >> $D${sysconfdir}/sgml/catalog
	fi

	if ! grep -q ${sysconfdir}/sgml/sgml-ent.cat $D${sysconfdir}/sgml/sgml-docbook.cat; then
	    echo "CATALOG ${sysconfdir}/sgml/sgml-ent.cat" >> $D${sysconfdir}/sgml/sgml-docbook.cat
	fi
	if ! grep -q ${sysconfdir}/sgml/sgml-docbook.cat $D${sysconfdir}/sgml/catalog; then
	    echo "CATALOG ${sysconfdir}/sgml/sgml-docbook.cat" >> $D${sysconfdir}/sgml/catalog
	fi
    fi
}

pkg_postrm_${PN}() {
	install-catalog \
		--remove ${sysconfdir}/sgml/sgml-ent.cat \
		${datadir}/sgml/sgml-iso-entities-8879.1986/catalog

	install-catalog \
		--remove ${sysconfdir}/sgml/sgml-docbook.cat \
		${sysconfdir}/sgml/sgml-ent.cat
}
