SUMMARY = "Module::Runtime - runtime module handling"
DESCRIPTION = "The functions exported by this module deal with runtime \
handling of Perl modules, which are normally handled at compile time. This \
module avoids using any other modules, so that it can be used in low-level \
infrastructure. \
The parts of this module that work with module names apply the same syntax \
that is used for barewords in Perl source. In principle this syntax can vary \
between versions of Perl, and this module applies the syntax of the Perl on \
which it is running. In practice the usable syntax hasn't changed yet, but \
there's a good chance of it changing in Perl 5.18. \
The functions of this module whose purpose is to load modules include \
workarounds for three old Perl core bugs regarding require. These workarounds \
are applied on any Perl version where the bugs exist, except for a case where \
one of the bugs cannot be adequately worked around in pure Perl."
SECTION = "libs"

HOMEPAGE = "http://search.cpan.org/~zefram/Module-Runtime/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=45;endline=46;md5=62e24a93342fede7221d66335c716f34"

SRCNAME = "module-runtime"
SRC_URI = "https://github.com/moto-timo/${SRCNAME}/archive/${PV}.tar.gz"
SRC_URI[md5sum] = "ad6ca179c978aa02ac8aa29244ef9beb"
SRC_URI[sha256sum] = "59effa82b3f6986d28de6154a8f2428157691004d951f936a81e851f4dbcb045"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit cpan

RDEPENDS_${PN} = " perl-module-test-more \
                   perl-module-strict \
"

BBCLASSEXTEND = "native"
