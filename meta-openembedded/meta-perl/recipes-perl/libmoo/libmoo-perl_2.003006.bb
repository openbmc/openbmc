SUMMARY = "Moo - Minimalist Object Orientation (with Moose compatibility)"
DESCRIPTION = "This module us an extremely light-weight subset of \"Moose\" \
optimised for rapid startup and \"pay only for what you use\"."

SECTION = "libs"

HOMEPAGE = "http://metapan.org/release/Moo/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=742;endline=787;md5=2ee6a628a04af08d75bc73a86da86d4a"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Moo-${PV}.tar.gz"
SRC_URI[md5sum] = "53af7e7849492a8e3994ee379d12e348"
SRC_URI[sha256sum] = "bcb2092ab18a45005b5e2e84465ebf3a4999d8e82a43a09f5a94d859ae7f2472"

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
