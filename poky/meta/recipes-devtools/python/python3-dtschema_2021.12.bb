DESCRIPTION = "Tooling for devicetree validation using YAML and jsonschema"
HOMEPAGE = "https://github.com/devicetree-org/dt-schema"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=457495c8fa03540db4a576bf7869e811"

inherit pypi setuptools3

PYPI_PACKAGE = "dtschema"

SRC_URI[sha256sum] = "f68af77fbce1ae00015c1fd2809fd20db8b72a27105c26a20e1ac0203aee1739"

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += "python3-ruamel-yaml python3-jsonschema python3-rfc3987"

BBCLASSEXTEND = "native nativesdk"
