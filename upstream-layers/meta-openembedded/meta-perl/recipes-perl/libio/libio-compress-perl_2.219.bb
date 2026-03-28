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

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=10;md5=a05fdd79b87a410b6178b73126ffafdd"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/IO-Compress-${PV}.tar.gz"

SRC_URI[sha256sum] = "d3f509c4ec2f75d3ea6621b96aef61329a23711c11efb2222c837db1370433e9"

S = "${UNPACKDIR}/IO-Compress-${PV}"

inherit cpan

RDEPENDS:${PN} += "\
    perl-module-bytes \
    perl-module-cwd \
    perl-module-encode \
    perl-module-file-glob \
    perl-module-file-spec \
    perl-module-posix \
    perl-module-scalar-util \
    perl-module-time-local \
    perl-module-utf8 \
    libcompress-raw-bzip2-perl \
    libcompress-raw-zlib-perl \
"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "39"

ALTERNATIVE:${PN} = "streamzip zipdetails"
ALTERNATIVE_LINK_NAME[streamzip] = "${bindir}/streamzip"
ALTERNATIVE_LINK_NAME[zipdetails] = "${bindir}/zipdetails"

BBCLASSEXTEND = "native"
