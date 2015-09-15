SUMMARY = "XSL stylesheets for processing DocBook XML to various output formats"
HOMEPAGE = "http://docbook.sourceforge.net"
LICENSE = "XSL"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6eeeed43d498c22a835382533356462"

SRC_URI = "${SOURCEFORGE_MIRROR}/docbook/docbook-xsl-${PV}.tar.bz2 \
           file://docbook-xsl.xml \
           file://docbook-xsl-stylesheets-no-bashism-in-docbook-xsl-up.patch \
"

SRC_URI[md5sum] = "6dd0f89131cc35bf4f2ed105a1c17771"
SRC_URI[sha256sum] = "c98f7296ab5c8ccd2e0bc07634976a37f50847df2d8a59bdb1e157664700b467"

S = "${WORKDIR}/docbook-xsl-${PV}"

inherit allarch
BBCLASSEXTEND = "native"

SSTATEPOSTINSTFUNCS_append_class-native = " docbook_xsl_stylesheets_sstate_postinst"
SYSROOT_PREPROCESS_FUNCS_append_class-native = " docbook_xsl_stylesheets_sysroot_preprocess"

do_configure (){
	:
}

do_compile (){
	:
}

do_install () {
	# Refer to http://www.linuxfromscratch.org/blfs/view/stable/pst/docbook-xsl.html
	# for details.
	install -v -m755 -d ${D}${datadir}/xml/docbook/xsl-stylesheets-1.78.1

	cp -v -R VERSION common eclipse epub extensions fo highlighting html \
		htmlhelp images javahelp lib manpages params profiling \
		roundtrip slides template tests tools webhelp website \
		xhtml xhtml-1_1 catalog.xml \
	${D}${datadir}/xml/docbook/xsl-stylesheets-1.78.1

	ln -s VERSION ${D}/${datadir}/xml/docbook/xsl-stylesheets-1.78.1/VERSION.xsl

	install -v -m644 -D README \
		${D}${datadir}/doc/docbook-xsl-1.78.1/README.txt
	install -v -m644    RELEASE-NOTES* NEWS* \
		${D}${datadir}/doc/docbook-xsl-1.78.1

	install -d ${D}${sysconfdir}/xml/
	install -m 755  ${WORKDIR}/docbook-xsl.xml ${D}${sysconfdir}/xml/docbook-xsl.xml

}

docbook_xsl_stylesheets_sstate_postinst () {
    if [ "${BB_CURRENTTASK}" = "populate_sysroot" -o "${BB_CURRENTTASK}" = "populate_sysroot_setscene" ]
    then
        # Ensure that the catalog file sgml-docbook.cat is properly
        # updated when the package is installed from sstate cache.
        sed -i -e "s|file://.*/usr/share/xml|file://${datadir}/xml|g" ${SYSROOT_DESTDIR}${sysconfdir}/xml/docbook-xsl.xml
    fi
}

docbook_xsl_stylesheets_sysroot_preprocess () {
    # Update the hardcode dir in docbook-xml.xml
    sed -i -e "s|file:///usr/share/xml|file://${datadir}/xml|g" ${SYSROOT_DESTDIR}${sysconfdir}/xml/docbook-xsl.xml
}

RDEPENDS_${PN} += "perl"
FILES_${PN} = "${datadir}/xml/* ${sysconfdir}/xml/docbook-xsl.xml"
FILES_${PN}-doc = "${datadir}/doc/*"
