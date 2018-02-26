SUMMARY = "Role::Tiny - Roles. Like a nouvelle cousine portion size of Moose."
DESCRIPTION = "\"Role::Tiny\" is a minimalist role composition tool."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/pod/Role::Tiny"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=177;endline=214;md5=26df7e7c20551fb1906e2286624f0b71"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Role-Tiny-${PV}.tar.gz"
SRC_URI[md5sum] = "c8c5cf1e02d2e0a1ed22530b55b67d66"
SRC_URI[sha256sum] = "593a29b621e029bf0218d0154d5dfdf6ec502afc49adeeadae6afd0c70063115"

S = "${WORKDIR}/Role-Tiny-${PV}"

inherit cpan

RDEPENDS_${PN} = " perl-module-exporter \
                   perl-module-strict \
                   perl-module-test-more \
                   perl-module-warnings \
"

RPROVIDES_${PN} = " librole-tiny-perl \
                    librole-tiny-with-perl \
"

BBCLASSEXTEND = "native"
