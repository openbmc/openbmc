# NOTE:
#    You should use perl-module-encode rather than this package
#    unless you specifically need a version newer than what is
#    provided by perl.

SUMMARY = "Encode - character encodings"
DESCRIPTION = "The \"Encode\" module provides the interfaces between \
Perl's strings and the rest of the system.  Perl strings are sequences \
of characters."

AUTHOR = "Dan Kogai <dankogai+cpan@gmail.com>"
HOMEPAGE = "https://metacpan.org/release/Encode"
SECTION = "lib"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://META.json;md5=d8e909447b983532b2b460c830e7a7e4"

SRC_URI = "${CPAN_MIRROR}/authors/id/D/DA/DANKOGAI/Encode-${PV}.tar.gz"
SRC_URI[md5sum] = "f995e0eb9e52d01ed57abe835bf3ccb6"
SRC_URI[sha256sum] = "acb3a4af5e3ee38f94de8baa7454e0b836a0649e7ac4180f28dfca439ad60cff"

UPSTREAM_CHECK_REGEX = "Encode\-(?P<pver>(\d+\.\d+))(?!_\d+).tar"

S = "${WORKDIR}/Encode-${PV}"

inherit cpan

#  file /usr/bin/enc2xs from install of perl-misc-5.24.1-r0.i586 conflicts with file from package libencode-perl-2.94-r0.i586
#  file /usr/bin/encguess from install of perl-misc-5.24.1-r0.i586 conflicts with file from package libencode-perl-2.94-r0.i586
#  file /usr/bin/piconv from install of perl-misc-5.24.1-r0.i586 conflicts with file from package libencode-perl-2.94-r0.i586
RCONFLICTS_${PN} = "perl-misc"

RDEPENDS_${PN} += " perl-module-bytes \
                    perl-module-constant \
                    perl-module-parent \
                    perl-module-storable \
                    perl-module-xsloader \
"

RPROVIDES_${PN} += "libencode-alias-perl \
                    libencode-byte-perl \
                    libencode-cjkconstants-perl \
                    libencode-cn-perl \
                    libencode-cn-hz-perl \
                    libencode-config-perl \
                    libencode-ebcdic-perl \
                    libencode-encoder-perl \
                    libencode-encoding-perl \
                    libencode-gsm0338-perl \
                    libencode-guess-perl \
                    libencode-jp-perl \
                    libencode-jp-h2z-perl \
                    libencode-jp-jis7-perl \
                    libencode-kr-perl \
                    libencode-kr-2022_kr-perl \
                    libencode-mime-header-perl \
                    libencode-mime-name-perl \
                    libencode-symbol-perl \
                    libencode-tw-perl \
                    libencode-unicode-perl \
                    libencode-unicode-utf7-perl \
                    libencoding-perl \
                    libencode-internal-perl \
                    libencode-mime-header-iso_2022_jp-perl \
                    libencode-utf8-perl \
                    libencode-utf_ebcdic-perl \
                    "

BBCLASSEXTEND = "native"
