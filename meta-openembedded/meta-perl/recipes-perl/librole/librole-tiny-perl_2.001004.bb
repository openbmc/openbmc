SUMMARY = "Role::Tiny - Roles. Like a nouvelle cousine portion size of Moose."
DESCRIPTION = "\"Role::Tiny\" is a minimalist role composition tool."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/pod/Role::Tiny"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=182;endline=219;md5=26df7e7c20551fb1906e2286624f0b71"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Role-Tiny-${PV}.tar.gz"
SRC_URI[md5sum] = "98446826608b1e943e65c1f6e35942fe"
SRC_URI[sha256sum] = "92ba5712850a74102c93c942eb6e7f62f7a4f8f483734ed289d08b324c281687"

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
