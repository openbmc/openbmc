DESCRIPTION = "Tooling for devicetree validation using YAML and jsonschema"
HOMEPAGE = "https://github.com/devicetree-org/dt-schema"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=457495c8fa03540db4a576bf7869e811"

inherit pypi setuptools3

PYPI_PACKAGE = "dtschema"

SRC_URI[sha256sum] = "2238753fa16bee7b26841cced75d745af777b896190f40aff5802992335201bb"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "python3-ruamel-yaml python3-jsonschema python3-rfc3987"

BBCLASSEXTEND = "native nativesdk"
