SUMMARY = "Syntax highlight utility"
DESCRIPTION = "Source-highlight converts source code to formatted text with syntax highlighting."
HOMEPAGE = "https://www.gnu.org/software/src-highlite/"
LICENSE = "GPL-3.0-only"
SECTION = "libs"
LIC_FILES_CHKSUM = "file://COPYING;md5=ff95bfe019feaf92f524b73dd79e76eb"

SRCREV = "894cacd0799ca60afa359a63782729dec76cbb79"
PV = "3.1.9+git"
SRC_URI = "git://git.savannah.gnu.org/git/src-highlite.git;protocol=https;branch=master"

inherit autotools pkgconfig

DEPENDS:append = " bison-native boost"

DEPENDS:append:class-target = " ${BPN}-native"

EXTRA_OECONF = "--with-boost-regex=boost_regex"

BBCLASSEXTEND = "native nativesdk"

PACKAGES += "${PN}-tools ${PN}-data"

RDEPENDS:${PN} = "boost-regex ${PN}-data"
RDEPENDS:${PN}-tools = "${PN} ${PN}-data bash"

FILES:${PN}       = "${libdir}/*${SOLIBS}"
FILES:${PN}-data  = "${datadir}/source-highlight"
FILES:${PN}-tools = "${bindir} ${sysconfdir}/bash_completion.d"

# source-highlight is using its own binary from the build tree to make documentation
# let's substitute the native binary instead
do_configure:prepend:class-target () {
     sed -i -e 's,^SRCHILITEEXE = $(top_builddir).*,SRCHILITEEXE = source-highlight,' ${S}/doc/Makefile.am
}
