SUMMARY = "REPL plugin for Click"
HOMEPAGE = "https://github.com/untitaker/click-repl"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=580facc4832cac548fad94845542da44"

SRC_URI[sha256sum] = "17849c23dba3d667247dc4defe1757fff98694e90fe37474f3feebb69ced26a9"

inherit pypi setuptools3

RDEPENDS:${PN} = "${PYTHON_PN}-click ${PYTHON_PN}-prompt-toolkit"
