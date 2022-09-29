SUMMARY = "A shell-script tool for converting XML files to various formats"
HOMEPAGE = "https://pagure.io/xmlto"
DESCRIPTION = "Utility xmlto is a simple shell-script tool for converting XML files to various formats. It serves as easy to use command line frontend to make fine output without remembering many long options and searching for the syntax of the backends."
SECTION = "docs/xmlto"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SRCREV = "6fa6a0e07644f20abf2596f78a60112713e11cbe"
UPSTREAM_CHECK_COMMITS = "1"
SRC_URI = "git://pagure.io/xmlto.git;protocol=https;branch=master \
           file://configure.in-drop-the-test-of-xmllint-and-xsltproc.patch \
           file://0001-Skip-validating-xmlto-output.patch \
"
S = "${WORKDIR}/git"

PV .= "+0.0.29+git${SRCPV}"

inherit autotools

CLEANBROKEN = "1"

DEPENDS = "libxml2-native libxslt-native flex-native docbook-xml-dtd4-native docbook-xsl-stylesheets-native"

RDEPENDS:${PN} = "docbook-xml-dtd4 \
                  docbook-xsl-stylesheets \
                  util-linux \
                  libxml2 \
                  libxslt \
                  bash \
"
RDEPENDS:${PN}:append:class-target = " \
                  libxml2-utils \
                  libxslt-bin \
                  coreutils \
"
CACHED_CONFIGUREVARS += "ac_cv_path_TAIL=tail ac_cv_path_GREP=grep"

BBCLASSEXTEND = "native"

EXTRA_OECONF:append = " BASH=/bin/bash GCP=/bin/cp XMLLINT=xmllint XSLTPROC=xsltproc"

do_configure:prepend() {
    (cd ${S} && flex -o xmlif/xmlif.c xmlif/xmlif.l)
}

do_install:append:class-native() {
    create_wrapper ${D}${bindir}/xmlto XML_CATALOG_FILES=${sysconfdir}/xml/catalog
}

do_populate_sysroot[rdeptask] = "do_populate_sysroot"
