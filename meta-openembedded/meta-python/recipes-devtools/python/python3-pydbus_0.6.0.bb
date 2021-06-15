DESCRIPTION = "Pythonic DBus library"
HOMEPAGE = "https://pypi.python.org/pypi/pydbus/"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a916467b91076e631dd8edb7424769c7"

SRC_URI += "file://0001-Support-asynchronous-calls-58.patch \
            file://0002-Support-transformation-between-D-Bus-errors-and-exce.patch \
"

SRC_URI[md5sum] = "c6abd44862322679bd4e907bebc3e0d0"
SRC_URI[sha256sum] = "4207162eff54223822c185da06c1ba8a34137a9602f3da5a528eedf3f78d0f2c"

inherit pypi setuptools3

S = "${WORKDIR}/pydbus-${PV}"

RDEPENDS_${PN} = "${PYTHON_PN}-pygobject \
                  ${PYTHON_PN}-io \
                  ${PYTHON_PN}-logging"
