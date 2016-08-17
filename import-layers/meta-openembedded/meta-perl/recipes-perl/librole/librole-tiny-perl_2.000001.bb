SUMMARY = "Role::Tiny - Roles. Like a nouvelle cousine portion size of Moose."
DESCRIPTION = "\"Role::Tiny\" is a minimalist role composition tool."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/pod/Role::Tiny"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=172;endline=209;md5=26df7e7c20551fb1906e2286624f0b71"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Role-Tiny-${PV}.tar.gz"
SRC_URI[md5sum] = "f350f1f8c13652bf85da172380b39ec8"
SRC_URI[sha256sum] = "31883410a7c85d6dc7501c718b1f83edba013a7b9bbccf0338a1033c391f296d"

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
