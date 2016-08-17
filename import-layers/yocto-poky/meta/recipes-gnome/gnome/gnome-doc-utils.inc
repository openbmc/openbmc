SUMMARY = "A collection of documentation utilities for the Gnome project"
DESCRIPTION = "The GNOME Documentation Build Utilities (gnome-doc- \
utils) were created to make it easier for application developers to \
include and create documentation in their releases. They include the \
xml2po tool which makes it easier to translate and keep up to date \
translations of documentation."
LICENSE = "GPLv2 & LGPLv2.1"
DEPENDS = "libxml2 libxslt libxslt-native gnome-doc-utils-native glib-2.0"
DEPENDS_class-native = "libxml2-native libxslt-native intltool-native glib-2.0-native"

inherit gnomebase gettext python-dir pythonnative autotools-brokensep

CLEANBROKEN = "1"

EXTRA_OECONF += "--disable-scrollkeeper"

do_install_append() {
	chown -R root:root ${D}
}

do_install_append_class-native () {
	sed -i -e 's|^#!.*/usr/bin/env python|#! /usr/bin/env nativepython|' ${D}${bindir}/xml2po
}

FILES_${PN} += "${datadir}/xml* ${PYTHON_SITEPACKAGES_DIR}/*"

RDEPENDS_${PN} = "bash"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native"
