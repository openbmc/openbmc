SUMMARY = "ASPEED FMC image tool is to pack the SoC FMC image with the header \
for runtime FW load."

require fmc-imgtool.inc

inherit python3native python_setuptools_build_meta

DEPENDS += "python3-cryptography"
DEPENDS += "python3-pyhsslms"

RDEPENDS:${PN} += "python3-cryptography"
RDEPENDS:${PN} += "python3-pyhsslms"

BBCLASSEXTEND = "native nativesdk"
