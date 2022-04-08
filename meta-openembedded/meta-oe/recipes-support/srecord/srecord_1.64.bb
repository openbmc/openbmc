SUMMARY = "A collection of powerful tools for manipulating EPROM load files."
SECTION = "devel"
LICENSE = "GPL-3.0-or-later & LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8dfcbf2f0a144b97f0931b6394debea7"

SRC_URI = " \
    http://srecord.sourceforge.net/srecord-${PV}.tar.gz \
    file://add-option-to-remove-docs.patch \
    file://libtool.patch \
"

SRC_URI[md5sum] = "4de4a7497472d7972645c2af91313769"
SRC_URI[sha256sum] = "49a4418733c508c03ad79a29e95acec9a2fbc4c7306131d2a8f5ef32012e67e2"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/srecord/files/releases"

DEPENDS = "boost groff-native"

inherit autotools-brokensep

do_configure:prepend() {
    # To autoreconf we need the script in ${S}, we can't tell autotools to use
    # etc/ because then it can't find the Makefile.in
    ln -sf ${S}/etc/configure.ac ${S}
}

PACKAGECONFIG ??= "gcrypt"
PACKAGECONFIG[gcrypt] = "--with-gcrypt,--without-gcrypt,libgcrypt"

# Set variable WITHOUT_DOC=0 to enable documentation generation
EXTRA_OEMAKE = "WITHOUT_DOC=1"

BBCLASSEXTEND = "native"
