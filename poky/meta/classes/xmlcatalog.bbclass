DEPENDS = "libxml2-native"

# A whitespace-separated list of XML catalogs to be registered, for example
# "${sysconfdir}/xml/docbook-xml.xml".
XMLCATALOGS ?= ""

SYSROOT_PREPROCESS_FUNCS_append = " xmlcatalog_sstate_postinst"

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
