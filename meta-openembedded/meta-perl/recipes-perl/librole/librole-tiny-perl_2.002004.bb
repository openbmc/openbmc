SUMMARY = "Role::Tiny - Roles. Like a nouvelle cousine portion size of Moose."
DESCRIPTION = "\"Role::Tiny\" is a minimalist role composition tool."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/pod/Role::Tiny"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=197;endline=234;md5=26df7e7c20551fb1906e2286624f0b71"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Role-Tiny-${PV}.tar.gz"
SRC_URI[md5sum] = "9ee45591befa3d0b1094ac75d282b6ba"
SRC_URI[sha256sum] = "d7bdee9e138a4f83aa52d0a981625644bda87ff16642dfa845dcb44d9a242b45"

S = "${WORKDIR}/Role-Tiny-${PV}"

inherit cpan

RDEPENDS:${PN} = " perl-module-exporter \
                   perl-module-strict \
                   perl-module-test-more \
                   perl-module-warnings \
"

RPROVIDES:${PN} = " librole-tiny-perl \
                    librole-tiny-with-perl \
"

BBCLASSEXTEND = "native"
