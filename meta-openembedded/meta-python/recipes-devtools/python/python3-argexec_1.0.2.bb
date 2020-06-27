SUMMARY = "Expose your Python functions to the command line with one easy step!"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea70b07c354e36056bd35e17c9c3face"

inherit pypi setuptools3

SRC_URI[md5sum] = "9fac09884c54db79e57ab80f0c423794"
SRC_URI[sha256sum] = "e271286b280f930aeaae7496454573f8029c3f48ef1dc47c780155dd4a7b9e7f"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS_${PN} += "\
  python3-typing \
  python3-dynamic-dispatch \
  python3-typeguard \
"

BBCLASSEXTEND = "native nativesdk"
