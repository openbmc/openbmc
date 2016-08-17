SUMMARY = "Algorithm::Diff - Compute 'intelligent' differences between two \
files/lists"
DESCRIPTION = "This is a module for computing the difference between two files, \
two strings, or any other two lists of things.  It uses an  intelligent \
algorithm similar to (or identical to) the one used by the Unix `diff' \
program.   It is guaranteed to find the *smallest possible* set of \
differences. \
"
SECTION = "libs"
HOMEPAGE = "http://search.cpan.org/~nedkonz/Algorithm-Diff/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://lib/Algorithm/Diff.pm;beginline=406;endline=409;md5=d393b8ad3b8994b9d0ae9299b8a8a1ee"

SRC_URI = "${CPAN_MIRROR}/authors/id/N/NE/NEDKONZ/Algorithm-Diff-${PV}.tar.gz"
SRC_URI[md5sum] = "457cd497a0411a88b47d3741eb176071"
SRC_URI[sha256sum] = "aa848b75ad3ecc32d31e8651909551e851cceef74a32822c7a3cb35c259f5190"

S = "${WORKDIR}/Algorithm-Diff-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
