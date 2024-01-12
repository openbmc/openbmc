#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Note that this recipe only handles XML catalogues in the native sysroot, and doesn't
# yet support catalogue management in the target sysroot or on the target itself.
# (https://bugzilla.yoctoproject.org/13271)

# A whitespace-separated list of XML catalogs to be registered, for example
# "${sysconfdir}/xml/docbook-xml.xml".
XMLCATALOGS ?= ""

DEPENDS:append = " libxml2-native"

SYSROOT_PREPROCESS_FUNCS:append:class-native = " xmlcatalog_sstate_postinst"

xmlcatalog_complete() {
	ROOTCATALOG="${STAGING_ETCDIR_NATIVE}/xml/catalog"
	if [ ! -f $ROOTCATALOG ]; then
		mkdir --parents $(dirname $ROOTCATALOG)
		xmlcatalog --noout --create $ROOTCATALOG
	fi
	for CATALOG in ${XMLCATALOGS}; do
		xmlcatalog --noout --add nextCatalog unused file://$CATALOG $ROOTCATALOG
	done
}

xmlcatalog_sstate_postinst() {
	mkdir -p ${SYSROOT_DESTDIR}${bindir}
	dest=${SYSROOT_DESTDIR}${bindir}/postinst-${PN}-xmlcatalog
	echo '#!/bin/sh' > $dest
	echo '${xmlcatalog_complete}' >> $dest
	chmod 0755 $dest
}
