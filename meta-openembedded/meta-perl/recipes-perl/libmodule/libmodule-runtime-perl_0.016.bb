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

HOMEPAGE = "https://metacpan.org/dist/Module-Runtime"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=36;endline=44;md5=9416434672a57853d6181f3da9094963"

SRCNAME = "Module-Runtime"
SRC_URI = "${CPAN_MIRROR}/authors/id/Z/ZE/ZEFRAM/${SRCNAME}-${PV}.tar.gz"
SRC_URI[sha256sum] = "68302ec646833547d410be28e09676db75006f4aa58a11f3bdb44ffe99f0f024"

UPSTREAM_CHECK_REGEX = "Module\-Runtime\-(?P<pver>(\d+\.\d+)).tar"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit cpan

RDEPENDS:${PN} = " perl-module-test-more \
                   perl-module-strict \
"

BBCLASSEXTEND = "native"
