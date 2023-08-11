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

LIC_FILES_CHKSUM = "file://README;beginline=8;endline=10;md5=8e78b88f9c8ac08ae03e8ea81195ca45"

SRC_URI = "${CPAN_MIRROR}/authors/id/P/PM/PMQS/IO-Compress-${PV}.tar.gz"

SRC_URI[sha256sum] = "7d3062b9a494f757e8d0614f220d83f22731bbda1aeb5f7cff0e72a83f433d35"

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
    perl-module-time-local \
    perl-module-utf8 \
    libcompress-raw-bzip2-perl \
    libcompress-raw-zlib-perl \
"

BBCLASSEXTEND = "native"
