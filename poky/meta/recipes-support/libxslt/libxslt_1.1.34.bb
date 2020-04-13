SUMMARY = "GNOME XSLT library"
HOMEPAGE = "http://xmlsoft.org/XSLT/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://Copyright;md5=0cd9a07afbeb24026c9b03aecfeba458"

SECTION = "libs"
DEPENDS = "libxml2"

SRC_URI = "http://xmlsoft.org/sources/libxslt-${PV}.tar.gz \
          "

SRC_URI[md5sum] = "db8765c8d076f1b6caafd9f2542a304a"
SRC_URI[sha256sum] = "98b1bd46d6792925ad2dfe9a87452ea2adebf69dcb9919ffd55bf926a7f93f7f"

UPSTREAM_CHECK_REGEX = "libxslt-(?P<pver>\d+(\.\d+)+)\.tar"

S = "${WORKDIR}/libxslt-${PV}"

BINCONFIG = "${bindir}/xslt-config"

inherit autotools pkgconfig binconfig-disabled lib_package multilib_header

do_configure_prepend () {
	# We don't DEPEND on binutils for ansidecl.h so ensure we don't use the header.
	# This can be removed when upgrading to 1.1.34.
	sed -i -e 's/ansidecl.h//' ${S}/configure.ac

	# The timestamps in the 1.1.28 tarball are messed up causing this file to
	# appear out of date.  Touch it so that we don't try to regenerate it.
	touch ${S}/doc/xsltproc.1
}

EXTRA_OECONF = "--without-python --without-debug --without-mem-debug --without-crypto --with-html-subdir=${BPN}"
# older versions of this recipe had ${PN}-utils
RPROVIDES_${PN}-bin += "${PN}-utils"
RCONFLICTS_${PN}-bin += "${PN}-utils"
RREPLACES_${PN}-bin += "${PN}-utils"

# This is only needed until libxml can load the relocated catalog itself
do_install_append_class-native () {
    create_wrapper ${D}/${bindir}/xsltproc XML_CATALOG_FILES=${sysconfdir}/xml/catalog
}

do_install_append () {
   oe_multilib_header libxslt/xsltconfig.h
}

FILES_${PN} += "${libdir}/libxslt-plugins"
FILES_${PN}-dev += "${libdir}/xsltConf.sh"

BBCLASSEXTEND = "native nativesdk"
