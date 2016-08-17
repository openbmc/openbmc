SUMMARY = "Moo - Minimalist Object Orientation (with Moose compatibility)"
DESCRIPTION = "This module us an extremely light-weight subset of \"Moose\" \
optimised for rapid startup and \"pay only for what you use\"."

SECTION = "libs"

HOMEPAGE = "http://metapan.org/release/Moo/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=688;endline=733;md5=27efedd175eeaddbd18f4e3572bd72a8"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Moo-${PV}.tar.gz"
SRC_URI[md5sum] = "8b84a7289fc6247de5ec5d151105fd6b"
SRC_URI[sha256sum] = "fb4bfa751f0dd06bd70f2e06e811f85a640501f263c228a8efafbf6b26691fd4"

S = "${WORKDIR}/Moo-${PV}"

inherit cpan

RDEPENDS_${PN} = " libclass-method-modifiers-perl \
                   libdevel-globaldestruction-perl \
                   libmodule-runtime-perl \
                   librole-tiny-perl \
                   perl-module-constant \
                   perl-module-exporter \
                   perl-module-mro \
                   perl-module-scalar-util \
"

RPROVIDES_${PN} = " libmethod-inliner-perl \
                    libmethod-generate-accessor-perl \
                    libmethod-generate-buildall-perl \
                    libmethod-generate-constructor-perl \
                    libmethod-generate-demolishall-perl \
                    libmoo-perl \
                    libmoo-handlemoose-perl \
                    libmoo-handlemoose-fakemetaclass-perl \
                    libmoo-object-perl \
                    libmoo-role-perl \
                    libsub-defer-perl \
                    libsub-quote-perl \
"

BBCLASSEXTEND = "native"
