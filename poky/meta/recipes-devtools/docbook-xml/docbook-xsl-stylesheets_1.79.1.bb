SUMMARY = "XSL stylesheets for processing DocBook XML to various output formats"
HOMEPAGE = "http://docbook.sourceforge.net"
LICENSE = "DocBook-XML"
LIC_FILES_CHKSUM = "file://COPYING;md5=6beadd98f9c54ab0c387e14211ee4d0e"

SRC_URI = "${SOURCEFORGE_MIRROR}/docbook/docbook-xsl-${PV}.tar.bz2 \
           file://docbook-xsl-stylesheets-no-bashism-in-docbook-xsl-up.patch \
"

SRC_URI[md5sum] = "b48cbf929a2ad85e6672f710777ca7bc"
SRC_URI[sha256sum] = "725f452e12b296956e8bfb876ccece71eeecdd14b94f667f3ed9091761a4a968"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/docbook/files/docbook-xsl/"
# Reject versions ending in .0 as those are release candidates
UPSTREAM_CHECK_REGEX = "/docbook-xsl/(?P<pver>(\d+[\.\-_]*)+(?!\.0)\.\d+)/"

S = "${WORKDIR}/docbook-xsl-${PV}"

inherit allarch xmlcatalog

do_configure (){
	:
}

do_compile (){
	:
}

do_install () {
	install -v -m755 -d ${D}${datadir}/xml/docbook/xsl-stylesheets-${PV}
	ln -s xsl-stylesheets-${PV} ${D}${datadir}/xml/docbook/xsl-stylesheets

	cp -v -R VERSION assembly common eclipse epub epub3 fo \
		highlighting html htmlhelp images javahelp lib manpages \
		params profiling roundtrip slides template webhelp website \
		xhtml xhtml-1_1 xhtml5 catalog.xml \
		${D}${datadir}/xml/docbook/xsl-stylesheets-${PV}

	ln -s VERSION ${D}/${datadir}/xml/docbook/xsl-stylesheets-${PV}/VERSION.xsl

	install -d ${D}${docdir}/${BPN}
	install -v -m644 README RELEASE-NOTES* NEWS* ${D}${docdir}/${BPN}
}

RDEPENDS:${PN} += "perl"
FILES:${PN} = "${datadir}/xml/* ${sysconfdir}/xml/docbook-xsl.xml"
FILES:${PN}-doc = "${datadir}/doc/*"

XMLCATALOGS = "${datadir}/xml/docbook/xsl-stylesheets-${PV}/catalog.xml"

BBCLASSEXTEND = "native"
