SUMMARY = "Moo - Minimalist Object Orientation (with Moose compatibility)"
DESCRIPTION = "This module us an extremely light-weight subset of \"Moose\" \
optimised for rapid startup and \"pay only for what you use\"."

SECTION = "libs"

HOMEPAGE = "http://metapan.org/release/Moo/"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=742;endline=787;md5=0e7ee44f5ce5e9b84619cd198caad1d6"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Moo-${PV}.tar.gz"
SRC_URI[sha256sum] = "e3030b80bd554a66f6b3c27fd53b1b5909d12af05c4c11ece9a58f8d1e478928"

S = "${WORKDIR}/Moo-${PV}"

inherit cpan

RDEPENDS:${PN} = " libclass-method-modifiers-perl \
                   libdevel-globaldestruction-perl \
                   libmodule-runtime-perl \
                   librole-tiny-perl \
                   perl-module-constant \
                   perl-module-exporter \
                   perl-module-mro \
                   perl-module-scalar-util \
"

RPROVIDES:${PN} = " libmethod-inliner-perl \
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
