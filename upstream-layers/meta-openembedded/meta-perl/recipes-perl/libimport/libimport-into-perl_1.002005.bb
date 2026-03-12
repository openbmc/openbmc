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

HOMEPAGE = "https://metacpan.org/pod/Import::Into"

LICENSE = "Artistic-1.0 | GPL-1.0-or-later"
LIC_FILES_CHKSUM = "file://README;beginline=209;endline=223;md5=3cf363f1e405dea6db2c6cd0ef23680c"

SRC_URI = "${CPAN_MIRROR}/authors/id/H/HA/HAARG/Import-Into-${PV}.tar.gz"
SRC_URI[sha256sum] = "bd9e77a3fb662b40b43b18d3280cd352edf9fad8d94283e518181cc1ce9f0567"

S = "${UNPACKDIR}/Import-Into-${PV}"

inherit cpan

RDEPENDS:${PN} = " libmodule-runtime-perl \
                   perl-module-strict \
                   perl-module-warnings \
"

BBCLASSEXTEND = "native"
