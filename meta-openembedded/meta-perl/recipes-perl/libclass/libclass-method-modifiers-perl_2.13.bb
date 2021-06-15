SUMMARY = "Class::Method::Modifiers - provides Moose-like method modifiers"
DESCRIPTION = "Method modifiers are a convenient feature from the CLOS \
(Common Lisp Object System) world."

SECTION = "libs"

HOMEPAGE = "https://github.com/moose/Class-Method-Modifiers/"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=16fd0ec7b73c0e158426f753943f1058"

SRC_URI = "${CPAN_MIRROR}/authors/id/E/ET/ETHER/Class-Method-Modifiers-${PV}.tar.gz"
SRC_URI[md5sum] = "b1398e3682aa2e075b913b9f9000b596"
SRC_URI[sha256sum] = "ab5807f71018a842de6b7a4826d6c1f24b8d5b09fcce5005a3309cf6ea40fd63"

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
