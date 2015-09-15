SUMMARY = "A shell-script tool for converting XML files to various formats"
HOMEPAGE = "https://fedorahosted.org/xmlto/"
SECTION = "docs/xmlto"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SRC_URI = "https://fedorahosted.org/releases/x/m/xmlto/xmlto-${PV}.tar.gz \
           file://configure.in-drop-the-test-of-xmllint-and-xsltproc.patch \
           file://catalog.xml \
"
SRC_URI[md5sum] = "0cca8be787ba01e00c618cb390c988b9"
SRC_URI[sha256sum] = "cfd8d2a26077be1d5566dfe22dd66099ae4f4600dea97d6e113a2cc5b8708977"

inherit autotools

# xmlto needs getopt/xmllint/xsltproc/bash at runtime
RDEPENDS_${PN} = "docbook-xml-dtd4 \
                  docbook-xsl-stylesheets \
                  util-linux \
                  libxml2 \
                  bash \
"
RDEPENDS_${PN}_append_class-target = " \
                  libxslt-bin \
"

BBCLASSEXTEND = "native"

EXTRA_OECONF_append = " XMLLINT=xmllint XSLTPROC=xsltproc"

do_install_append() {
    install -d ${D}${sysconfdir}/xml/
    install -m 755  ${WORKDIR}/catalog.xml ${D}${sysconfdir}/xml/catalog.xml
    create_wrapper ${D}/${bindir}/xmlto XML_CATALOG_FILES=${sysconfdir}/xml/catalog.xml
}

do_populate_sysroot[rdeptask] = "do_populate_sysroot"
