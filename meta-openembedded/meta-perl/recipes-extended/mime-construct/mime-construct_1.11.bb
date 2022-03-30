SUMMARY = "Construct and optionally mail MIME messages"
DESCRIPTION = "Constructs and (by default) mails MIME messages. \
               It is entirely driven from the command line, it is \
               designed to be used by other programs, or people who act \
               like programs."
HOMEPAGE = "http://search.cpan.org/~rosch/mime-construct/mime-construct"
SECTION = "mail"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=5e2e5da619ac8ef8c84767ccc4656e96"

SRC_URI = "${CPAN_MIRROR}/authors/id/R/RO/ROSCH/mime-construct-${PV}.tar.gz \
    file://fix-mime-construct-help-return-value.patch \
"

SRC_URI[md5sum] = "73834ea780fbea81b89dbd9b2fb54f58"
SRC_URI[sha256sum] = "4cd7bb61b51d41192d1498c1051aa6a4ccd75aeb09b71d2ec706a7084a4a9303"

inherit cpan

RDEPENDS:${PN} += "libmime-types-perl libproc-waitstat-perl msmtp \
    perl-module-filehandle perl-module-mime-base64 perl-module-mime-quotedprint perl-module-posix \
"

do_install:append() {
   #change the interpreter in file
   sed -i -e "s|${STAGING_BINDIR_NATIVE}/perl-native/perl -w|${bindir}/env perl|g" \
      ${D}/${bindir}/mime-construct
}
