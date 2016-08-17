SUMMARY = "Class::Method::Modifiers - provides Moose-like method modifiers"
DESCRIPTION = "Method modifiers are a convenient feature from the CLOS \
(Common Lisp Object System) world."

SECTION = "libs"

HOMEPAGE = "https://github.com/moose/Class-Method-Modifiers/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=16fd0ec7b73c0e158426f753943f1058"

SRC_URI = "${CPAN_MIRROR}/authors/id/E/ET/ETHER/Class-Method-Modifiers-${PV}.tar.gz"
SRC_URI[md5sum] = "f55400c7a8134acf3657f8af89bdd7af"
SRC_URI[sha256sum] = "e44c1073020bf55b8c97975ed77235fd7e2a6a56f29b5c702301721184e27ac8"

S = "${WORKDIR}/Class-Method-Modifiers-${PV}"

inherit cpan

RDEPENDS_${PN} = " perl-module-b \
                   perl-module-base \
                   perl-module-carp \
                   perl-module-exporter \
                   perl-module-strict \
                   perl-module-warnings \
"

BBCLASSEXTEND = "native"
