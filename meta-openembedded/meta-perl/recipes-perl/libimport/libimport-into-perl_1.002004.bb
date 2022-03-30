SUMMARY = "Import::Into - import packages into other packages"
DESCRIPTION = "Writing exporters is a pain. Some use \"Exporter\", some use \
\"Sub::Exporter\", some use \"Moose::Exporter\", some use \
\"Exporter::Declare\"... and some things are pragmas.\
\
Exporting on someone else's behalf is harder. The exporters don't provide a \
consistent API for this, and pragmas need to have their import method called \
directly, since they effect the current unit of compilation. \
\
\"Import::Into\" provides global methods to make this painless."

SECTION = "libs"

HOMEPAGE = "https://metacpan.org/pod/Import-Into/"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=209;endline=223;md5=3cf363f1e405dea6db2c6cd0ef23680c"

SRC_URI = "${CPAN_MIRROR}/authors/id/E/ET/ETHER/Import-Into-${PV}.tar.gz"
SRC_URI[md5sum] = "70f2f3b08a5b706ee382a8448c346cb1"
SRC_URI[sha256sum] = "decb259bc2ff015fe3dac85e4a287d4128e9b0506a0b2c5fa7244836a68b1084"

S = "${WORKDIR}/Import-Into-${PV}"

inherit cpan

RDEPENDS:${PN} = " libmodule-runtime-perl \
                   perl-module-strict \
                   perl-module-warnings \
"

BBCLASSEXTEND = "native"
