SUMMARY = "Algorithm::Diff - Compute 'intelligent' differences between two \
files/lists"
DESCRIPTION = "This is a module for computing the difference between two files, \
two strings, or any other two lists of things.  It uses an  intelligent \
algorithm similar to (or identical to) the one used by the Unix `diff' \
program.   It is guaranteed to find the *smallest possible* set of \
differences. \
"
SECTION = "libs"
HOMEPAGE = "https://metacpan.org/release/RJBS/Algorithm-Diff-1.201"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://lib/Algorithm/Diff.pm;beginline=1671;endline=1675;md5=f6b2fe8ca06ca6faefa4f265fc494c2c"

SRC_URI = "${CPAN_MIRROR}/authors/id/R/RJ/RJBS/Algorithm-Diff-${PV}.tar.gz"
SRC_URI[sha256sum] = "0022da5982645d9ef0207f3eb9ef63e70e9713ed2340ed7b3850779b0d842a7d"

S = "${UNPACKDIR}/Algorithm-Diff-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
