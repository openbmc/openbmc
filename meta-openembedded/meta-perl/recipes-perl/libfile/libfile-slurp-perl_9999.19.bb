SUMMARY = "Slurp entire files into variables."
DESCRIPTION = "This module provides subroutines to read or write \
  entire files with a simple call.  It also has a subroutine for \
  reading the list of filenames in a directory. \
"
SECTION = "libs"
HOMEPAGE = "https://metacpan.org/release/URI/File-Slurp-9999.19"
LICENSE = "Artistic-1.0 | GPL-1.0-or-later"

LIC_FILES_CHKSUM = "file://README;beginline=37;endline=41;md5=255fbd5f98a90d51d9908d31271ae4d4"
SRC_URI = "${CPAN_MIRROR}/authors/id/U/UR/URI/File-Slurp-${PV}.tar.gz"

S = "${WORKDIR}/File-Slurp-${PV}"

inherit cpan

SRC_URI[sha256sum] = "ce29ebe995097ebd6e9bc03284714cdfa0c46dc94f6b14a56980747ea3253643"

BBCLASSEXTEND="native"
