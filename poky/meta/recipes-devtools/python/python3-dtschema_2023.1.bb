DESCRIPTION = "Tooling for devicetree validation using YAML and jsonschema"
HOMEPAGE = "https://github.com/devicetree-org/dt-schema"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=457495c8fa03540db4a576bf7869e811"

inherit pypi setuptools3

PYPI_PACKAGE = "dtschema"

SRC_URI[sha256sum] = "8fc8c269e4c57e9d008af7b32cd33b77afd4ea1ac9552bcfa96b41b9e0c52586"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "python3-ruamel-yaml python3-jsonschema python3-rfc3987"

BBCLASSEXTEND = "native nativesdk"
