SUMMARY = "Perl module that provides simple filename and pathname matching"
DESCRIPTION = "File::FnMatch::fnmatch() provides simple, shell-like pattern \
matching. \
Though considerably less powerful than regular expressions, shell patterns \
are nonetheless useful and familiar to a large audience of end-users."

HOMEPAGE = "http://search.cpan.org/dist/File-FnMatch/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=34;endline=37;md5=54fdfac62963b7cece5583ab38f2cf0d"

SRC_URI = "https://cpan.metacpan.org/authors/id/M/MJ/MJP/File-FnMatch-${PV}.tar.gz"

SRC_URI[md5sum] = "22f77c20d0fb5af01a3165e2df2fe34c"
SRC_URI[sha256sum] = "962454b8e86bea8b132bf8af35757d0c6a8f5d599015bd6a5d68cb7ae7a9e916"

S = "${WORKDIR}/File-FnMatch-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
