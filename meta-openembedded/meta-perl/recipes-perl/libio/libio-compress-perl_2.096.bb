SUMMARY = "Perl interface to allow reading and writing of compressed data."
DESCRIPTION = "This distribution provides a Perl interface to allow reading \
and writing of compressed data created with the zlib and bzip2. \
\ 
IO-Compress supports reading and writing of the following compressed data \
formats \
  * bzip2 \
  * RFC 1950 \
  * RFC 1951 \
  * RFC 1952 (i.e. gzip) \
  * zip \
"
HOMEPAGE = "https://metacpan.org/release/IO-Compress"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=10;md5=36e282c4a4078cf2650d656cdda23210"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/IO-Compress-${PV}.tar.gz"

SRC_URI[md5sum] = "18ad197cad5ca87bc3a7d2538998e017"
SRC_URI[sha256sum] = "9d219fd5df4b490b5d2f847921e3cb1c3392758fa0bae9b05a8992b3620ba572"

S = "${WORKDIR}/IO-Compress-${PV}"

inherit cpan

RDEPENDS:${PN} += "\
    perl-module-bytes \
    perl-module-cwd \
    perl-module-encode \
    perl-module-file-glob \
    perl-module-file-spec \
    perl-module-posix \
    perl-module-scalar-util \
    perl-module-utf8 \
    libcompress-raw-bzip2-perl \
    libcompress-raw-zlib-perl \
"

BBCLASSEXTEND = "native"
