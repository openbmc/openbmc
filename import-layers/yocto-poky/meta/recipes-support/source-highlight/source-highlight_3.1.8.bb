SUMMARY = "Syntax highlight utility"
DESCRIPTION = "Source-highlight converts source code to formatted text with syntax highlighting."
HOMEPAGE = "https://www.gnu.org/software/src-highlite/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=ff95bfe019feaf92f524b73dd79e76eb"

SRC_URI = "${GNU_MIRROR}/src-highlite/${BPN}-${PV}.tar.gz \
           file://0001-source-highlight.pc.in-do-not-add-Boost-s-libraries-.patch"
SRC_URI[md5sum] = "3243470706ef5fefdc3e43b5306a4e41"
SRC_URI[sha256sum] = "01336a7ea1d1ccc374201f7b81ffa94d0aecb33afc7d6903ebf9fbf33a55ada3"

inherit autotools

DEPENDS_append = " boost"

DEPENDS_append_class-target = " source-highlight-native"

EXTRA_OECONF = "--with-boost=yes --with-boost-libdir=${STAGING_DIR_TARGET}${libdir}"

BBCLASSEXTEND = "native"

# source-highlight is using its own binary from the build tree to make documentation
# let's substitute the native binary instead
do_configure_prepend_class-target () {
        sed -i -e 's,^SRCHILITEEXE = $(top_builddir).*,SRCHILITEEXE = source-highlight,' ${S}/doc/Makefile.am
}

RDEPENDS_source-highlight += "bash"
