SUMMARY = "Class::Method::Modifiers - provides Moose-like method modifiers"
DESCRIPTION = "Method modifiers are a convenient feature from the CLOS \
(Common Lisp Object System) world."

SECTION = "libs"

HOMEPAGE = "https://github.com/moose/Class-Method-Modifiers/"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d49d07ae038f38923335ac8cc301e1ba"

SRC_URI = "${CPAN_MIRROR}/authors/id/E/ET/ETHER/Class-Method-Modifiers-${PV}.tar.gz"
SRC_URI[sha256sum] = "65cd85bfe475d066e9186f7a8cc636070985b30b0ebb1cde8681cf062c2e15fc"

S = "${WORKDIR}/Class-Method-Modifiers-${PV}"

inherit cpan

RDEPENDS:${PN} = " perl-module-b \
                   perl-module-base \
                   perl-module-carp \
                   perl-module-exporter \
                   perl-module-strict \
                   perl-module-warnings \
"

BBCLASSEXTEND = "native"
