SUMMARY = "Encode - character encodings"
DESCRIPTION = "The \"Encode\" module provides the interfaces between \
Perl's strings and the rest of the system.  Perl strings are sequences \
of characters."

AUTHOR = "Dan Kogai <dankogai+cpan@gmail.com>"
HOMEPAGE = "https://metacpan.org/release/Encode"
SECTION = "lib"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://META.json;md5=fdbebc82e925d8acbce42cfad131c4d1"

SRC_URI = "${CPAN_MIRROR}/authors/id/D/DA/DANKOGAI/Encode-${PV}.tar.gz"
SRC_URI[md5sum] = "0d3f59e8ea704497647eded665919053"
SRC_URI[sha256sum] = "5d3a90e30aabe78dfcf5e816ffb1da1e33475892dbd0075320315cdce5682988"

S = "${WORKDIR}/Encode-${PV}"

inherit cpan

RDEPENDS_${PN} += " perl-module-bytes \
                    perl-module-constant \
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
                    libencode-unicode--perl \
                    libencode-unicode-utf7-perl \
                    libencoding-perl \
                    libencode-internal-perl \
                    libencode-mime-header-iso_2022_jp-perl \
                    libencode-utf8-perl \
                    libencode-utf_ebcdic-perl \
                    "

BBCLASSEXTEND = "native"
