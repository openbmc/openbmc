SUMMARY = "XSL stylesheets for processing DocBook XML to various output formats"
HOMEPAGE = "http://docbook.sourceforge.net"
LICENSE = "XSL"
LIC_FILES_CHKSUM = "file://COPYING;md5=6beadd98f9c54ab0c387e14211ee4d0e"

SRC_URI = "${SOURCEFORGE_MIRROR}/docbook/docbook-xsl-${PV}.tar.bz2 \
           file://docbook-xsl.xml \
           file://docbook-xsl-stylesheets-no-bashism-in-docbook-xsl-up.patch \
"

SRC_URI[md5sum] = "b48cbf929a2ad85e6672f710777ca7bc"
SRC_URI[sha256sum] = "725f452e12b296956e8bfb876ccece71eeecdd14b94f667f3ed9091761a4a968"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/docbook/files/docbook-xsl/"
# Reject versions ending in .0 as those are release candidates
UPSTREAM_CHECK_REGEX = "/docbook-xsl/(?P<pver>(\d+[\.\-_]*)+(?!\.0)\.\d+)/"

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
	install -v -m755 -d ${D}${datadir}/xml/docbook/xsl-stylesheets-${PV}
	ln -s xsl-stylesheets-${PV} ${D}${datadir}/xml/docbook/xsl-stylesheets

	cp -v -R VERSION assembly common eclipse epub epub3 fo \
		highlighting html htmlhelp images javahelp lib manpages \
		params profiling roundtrip slides template webhelp website \
		xhtml xhtml-1_1 xhtml5 catalog.xml \
		${D}${datadir}/xml/docbook/xsl-stylesheets-${PV}

	ln -s VERSION ${D}/${datadir}/xml/docbook/xsl-stylesheets-${PV}/VERSION.xsl

	install -v -m644 -D README \
		${D}${datadir}/doc/docbook-xsl-${PV}/README.txt
	install -v -m644    RELEASE-NOTES* NEWS* \
		${D}${datadir}/doc/docbook-xsl-${PV}

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
