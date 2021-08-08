DESCRIPTION = "pytest-asyncio is an Apache2 licensed library, written in Python, for testing asyncio code with pytest"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[sha256sum] = "2564ceb9612bbd560d19ca4b41347b54e7835c2f792c504f698e05395ed63f6f"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-pytest \
"

BBCLASSEXTEND = "native nativesdk"
