SUMMARY = "Secure-boot utilities for ASPEED BMC SoCs"
HOMEPAGE = "https://github.com/AspeedTech-BMC/socsec/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d50b901333b4eedfee074ebcd6a6d611"

SRC_URI = "git://github.com/AspeedTech-BMC/socsec/;protocol=https;branch=develop"

PV = "1.7+git${SRCPV}"
SRCREV = "54cdd3b26262e66d2020a04cba55bc8b02090f1a"

S = "${WORKDIR}/git"

inherit python3native setuptools3

RDEPENDS_${PN} += "python3-bitarray"
RDEPENDS_${PN} += "python3-core"
RDEPENDS_${PN} += "python3-hexdump"
RDEPENDS_${PN} += "python3-jsonschema"
RDEPENDS_${PN} += "python3-jstyleson"
RDEPENDS_${PN} += "python3-pycryptodome"

BBCLASSEXTEND = "native nativesdk"
