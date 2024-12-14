SUMMARY = "GNOME XSLT library"
DESCRIPTION = "libxslt is the XSLT C parser and toolkit developed for the Gnome project. \
XSLT itself is a an XML language to define transformation for XML. Libxslt is based on \
libxml2 the XML C library developed for the GNOME project. It also implements most of \
the EXSLT set of processor-portable extensions functions and some of Saxon's evaluate \
and expressions extensions."
HOMEPAGE = "http://xmlsoft.org/XSLT/"
BUGTRACKER = "https://bugzilla.gnome.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://Copyright;md5=0cd9a07afbeb24026c9b03aecfeba458"

SECTION = "libs"
DEPENDS = "libxml2"

SRC_URI = "https://download.gnome.org/sources/libxslt/1.1/libxslt-${PV}.tar.xz"

SRC_URI[sha256sum] = "85ca62cac0d41fc77d3f6033da9df6fd73d20ea2fc18b0a3609ffb4110e1baeb"

UPSTREAM_CHECK_REGEX = "libxslt-(?P<pver>\d+(\.\d+)+)\.tar"

CVE_STATUS[CVE-2022-29824] = "not-applicable-config: Static linking to libxml2 is not enabled."

S = "${WORKDIR}/libxslt-${PV}"

BINCONFIG = "${bindir}/xslt-config"

inherit autotools pkgconfig binconfig-disabled lib_package multilib_header

do_configure:prepend () {
	# We don't DEPEND on binutils for ansidecl.h so ensure we don't use the header.
	# This can be removed when upgrading to 1.1.34.
	sed -i -e 's/ansidecl.h//' ${S}/configure.ac

	# The timestamps in the 1.1.28 tarball are messed up causing this file to
	# appear out of date.  Touch it so that we don't try to regenerate it.
	touch ${S}/doc/xsltproc.1
}

EXTRA_OECONF = "--without-python --without-debug --without-crypto"
# older versions of this recipe had ${PN}-utils
RPROVIDES:${PN}-bin += "${PN}-utils"
RCONFLICTS:${PN}-bin += "${PN}-utils"
RREPLACES:${PN}-bin += "${PN}-utils"

# This is only needed until libxml can load the relocated catalog itself
do_install:append:class-native () {
    create_wrapper ${D}/${bindir}/xsltproc XML_CATALOG_FILES=${sysconfdir}/xml/catalog
}

do_install:append () {
   oe_multilib_header libxslt/xsltconfig.h
}

FILES:${PN} += "${libdir}/libxslt-plugins"
FILES:${PN}-dev += "${libdir}/xsltConf.sh"

BBCLASSEXTEND = "native nativesdk"
