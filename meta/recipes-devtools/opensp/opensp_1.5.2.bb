SUMMARY = "An SGML parser"
DESCRIPTION = "An SGML parser used by the OpenJade suite of utilities."
HOMEPAGE = "http://openjade.sourceforge.net"
SECTION = "libs"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=641ff1e4511f0a87044ad42f87cb1045"

PR = "r1"

# At -Os it encounters calls to some inline functions which are then
# not found in any other objects with gcc 4.5
FULL_OPTIMIZATION += "-O2"

SRC_URI = "${SOURCEFORGE_MIRROR}/openjade/OpenSP-${PV}.tar.gz \
           file://obsolete_automake_macros.patch \
"

SRC_URI[md5sum] = "670b223c5d12cee40c9137be86b6c39b"
SRC_URI[sha256sum] = "57f4898498a368918b0d49c826aa434bb5b703d2c3b169beb348016ab25617ce"

S = "${WORKDIR}/OpenSP-${PV}"

inherit autotools gettext

EXTRA_OECONF = "--disable-doc-build"

EXTRA_OECONF_class-native = "\
	--disable-doc-build \
	--enable-default-catalog=${sysconfdir}/sgml/catalog \
	--enable-default-search-path=${datadir}/sgml \
	"

do_install_append() {
	# Set up symlinks to often-used alternate names. See
	# http://www.linuxfromscratch.org/blfs/view/stable/pst/opensp.html
	cd ${D}${libdir}
	ln -sf libosp.so libsp.so

	cd ${D}${bindir}
	for util in nsgmls sgmlnorm spam spcat spent sx; do
		ln -sf o$util $util	
	done
	ln -sf osx sgml2xml
}

do_install_append_class-native() {
	for util in nsgmls sgmlnorm spam spcat spent sx; do
		create_cmdline_wrapper ${D}/${bindir}/$util \
		    -D ${sysconfdir}/sgml
	done
}

FILES_${PN} += "${datadir}/OpenSP/"

BBCLASSEXTEND = "native"
