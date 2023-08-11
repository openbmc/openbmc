SUMMARY = "Automatically give your module the ability to have plugins"
DESCRIPTION = "Provides a simple but, hopefully, extensible way of \
having 'plugins' for your module. Obviously this isn't going to be the \
be all and end all of solutions but it works for me.\
\
Essentially all it does is export a method into your namespace that \
looks through a search path for .pm files and turn those into class \
names.\
\
Optionally it instantiates those classes for you."
SECTION = "libs"

HOMEPAGE = "https://github.com/simonwistow/Module-Pluggable"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=322;endline=325;md5=086450ce010f6fda25db0b38fcc41086"

SRCNAME = "Module-Pluggable"
SRC_URI = "${CPAN_MIRROR}/authors/id/S/SI/SIMONW/${SRCNAME}-${PV}.tar.gz"
SRC_URI[md5sum] = "87ce2971662efd0b69a81bb4dc9ea76c"
SRC_URI[sha256sum] = "b3f2ad45e4fd10b3fb90d912d78d8b795ab295480db56dc64e86b9fa75c5a6df"

PR = "r1"

UPSTREAM_CHECK_REGEX = "Module\-Pluggable\-(?P<pver>(\d+\.\d+))"
UPSTREAM_CHECK_URI = "https://metacpan.org/release/${SRCNAME}"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit cpan

RDEPENDS:${PN} = " perl-module-base \
    perl-module-deprecate \
    perl-module-file-basename \
    perl-module-file-find \
    perl-module-file-spec \
    perl-module-file-spec-functions \
    perl-module-if \
    perl-module-test-more \
"

BBCLASSEXTEND = "native"

