SUMMARY = "Perl module that run many filetest checks on a tree"
DESCRIPTION = "The validate() routine takes a single multiline string consisting \
of directives, each containing a filename plus a file test to try on it. (The file \
test may also be a "cd", causing subsequent relative filenames to be interpreted \
relative to that directory.) After the file test you may put || die to make it a \
fatal error if the file test fails. The default is || warn. The file test may \
optionally have a "!' prepended to test for the opposite condition. If you do a \
cd and then list some relative filenames, you may want to indent them slightly for \
readability. If you supply your own die() or warn() message, you can use $file to \
interpolate the filename. \
\
Filetests may be bunched: "-rwx" tests for all of -r, -w, and -x. Only the first failed \
test of the bunch will produce a warning. \
\
The routine returns the number of warnings issued."

HOMEPAGE = "http://search.cpan.org/~flora/File-CheckTree/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c6fcacc5df80e037060300a7f4b93bf9"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/F/FL/FLORA/File-CheckTree-${PV}.tar.gz"

SRC_URI[md5sum] = "519c82aa7e5b7f752b4da14a6c8ad740"
SRC_URI[sha256sum] = "fc99ab6bb5af4664832715974b5a19e328071dc9202ab72e5d5a594ebd46a729"

S = "${WORKDIR}/File-CheckTree-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
