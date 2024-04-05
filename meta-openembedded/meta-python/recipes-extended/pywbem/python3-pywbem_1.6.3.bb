SUMMARY = "Python WBEM Client and Provider Interface"
DESCRIPTION = "\
A Python library for making CIM (Common Information Model) operations over \
HTTP using the WBEM CIM-XML protocol. It is based on the idea that a good \
WBEM client should be easy to use and not necessarily require a large amount \
of programming knowledge. It is suitable for a large range of tasks from \
simply poking around to writing web and GUI applications. \
\
WBEM, or Web Based Enterprise Management is a manageability protocol, like \
SNMP, standardised by the Distributed Management Task Force (DMTF) available \
at http://www.dmtf.org/standards/wbem. \
\
It also provides a Python provider interface, and is the fastest and easiest \
way to write providers on the planet."
HOMEPAGE = "http://pywbem.github.io"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=fbc093901857fcd118f065f900982c24"

SRC_URI[sha256sum] = "78df30dee98c508620b599b8951f322a81c6c0a9d7b78ffe5e017b9417cd97b9"

inherit pypi setuptools3 update-alternatives

DEPENDS += " \
    python3-ply-native \
    python3-pyyaml-native \
    python3-six-native \
    python3-wheel-native \
"

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-io \
    python3-logging \
    python3-netclient \
    python3-nocasedict \
    python3-nocaselist \
    python3-ply \
    python3-pyyaml \
    python3-requests \
    python3-six \
    python3-stringold \
    python3-threading \
    python3-unixadmin \
    python3-xml \
    python3-yamlloader \
"

ALTERNATIVE:${PN} = "mof_compiler"
ALTERNATIVE_TARGET[mof_compiler] = "${bindir}/mof_compiler"

ALTERNATIVE_PRIORITY = "60"
