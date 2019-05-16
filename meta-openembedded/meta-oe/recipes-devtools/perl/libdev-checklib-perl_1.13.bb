SUMMARY = "A Perl module that checks whether a particular C library and its headers are available"
DESCRIPTION = "This module provides a way of checking whether a particular library \
and its headers are available, by attempting to compile a simple program and \
link against it."
HOMEPAGE = "https://metacpan.org/pod/Devel::CheckLib"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;md5=7911cdbb572d25c5f2e2ea17f669efc2"

SRC_URI = "https://cpan.metacpan.org/modules/by-module/Devel/Devel-CheckLib-${PV}.tar.gz \
           file://0001-CheckLib.pm-don-t-execute-the-binary.patch \
"
SRC_URI[md5sum] = "930216c5abc0f016df8a7539d48c891a"
SRC_URI[sha256sum] = "24adfd908705dfac4bb320711763f37126b75cf54b3566c2c27c7cdef9c429a8"

S = "${WORKDIR}/Devel-CheckLib-${PV}"

inherit cpan

BBCLASSEXTEND="native"
