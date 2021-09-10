SUMMARY = "Secure-boot utilities for ASPEED BMC SoCs"
HOMEPAGE = "https://github.com/AspeedTech-BMC/socsec/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d50b901333b4eedfee074ebcd6a6d611"

SRC_URI = "git://github.com/AspeedTech-BMC/socsec/;protocol=https;branch=master"

PV = "0.2.1+git${SRCPV}"
SRCREV = "51bd4504bffb5044264cfe4c63b8fe8d48a114a9"

S = "${WORKDIR}/git"

inherit python3native setuptools3

RDEPENDS:${PN} += "python3-bitarray"
RDEPENDS:${PN} += "python3-core"
RDEPENDS:${PN} += "python3-ecdsa"
RDEPENDS:${PN} += "python3-jsonschema"
RDEPENDS:${PN} += "python3-jstyleson"
RDEPENDS:${PN} += "python3-pycryptodome"

BBCLASSEXTEND = "native nativesdk"
