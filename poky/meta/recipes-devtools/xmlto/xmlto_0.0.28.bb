SUMMARY = "A shell-script tool for converting XML files to various formats"
HOMEPAGE = "https://pagure.io/xmlto"
DESCRIPTION = "Utility xmlto is a simple shell-script tool for converting XML files to various formats. It serves as easy to use command line frontend to make fine output without remembering many long options and searching for the syntax of the backends."
SECTION = "docs/xmlto"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SRC_URI = "https://releases.pagure.org/xmlto/xmlto-${PV}.tar.gz \
           file://configure.in-drop-the-test-of-xmllint-and-xsltproc.patch \
"
SRC_URI[md5sum] = "a1fefad9d83499a15576768f60f847c6"
SRC_URI[sha256sum] = "2f986b7c9a0e9ac6728147668e776d405465284e13c74d4146c9cbc51fd8aad3"

inherit autotools

CLEANBROKEN = "1"

DEPENDS = "libxml2-native"

RDEPENDS_${PN} = "docbook-xml-dtd4 \
                  docbook-xsl-stylesheets \
                  util-linux \
                  libxml2 \
                  libxslt \
                  bash \
"
RDEPENDS_${PN}_append_class-target = " \
                  libxml2-utils \
                  libxslt-bin \
                  coreutils \
"
CACHED_CONFIGUREVARS += "ac_cv_path_TAIL=tail ac_cv_path_GREP=grep"

BBCLASSEXTEND = "native"

EXTRA_OECONF_append = " BASH=/bin/bash GCP=/bin/cp XMLLINT=xmllint XSLTPROC=xsltproc"

do_install_append_class-native() {
    create_wrapper ${D}${bindir}/xmlto XML_CATALOG_FILES=${sysconfdir}/xml/catalog
}

do_populate_sysroot[rdeptask] = "do_populate_sysroot"
