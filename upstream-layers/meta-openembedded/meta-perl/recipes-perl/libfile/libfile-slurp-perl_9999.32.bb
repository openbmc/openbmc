SUMMARY = "Slurp entire files into variables."
DESCRIPTION = "This module provides subroutines to read or write \
  entire files with a simple call.  It also has a subroutine for \
  reading the list of filenames in a directory. \
"
SECTION = "libs"
HOMEPAGE = "https://metacpan.org/dist/File-Slurp"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README.md;beginline=488;endline=493;md5=7ac91b9bf12fda89deba7383172cbefe"
SRC_URI = "${CPAN_MIRROR}/authors/id/C/CA/CAPOEIRAB/File-Slurp-${PV}.tar.gz"

S = "${UNPACKDIR}/File-Slurp-${PV}"

inherit cpan

SRC_URI[sha256sum] = "4c3c21992a9d42be3a79dd74a3c83d27d38057269d65509a2f555ea0fb2bc5b0"

BBCLASSEXTEND = "native"
