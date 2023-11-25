SUMMARY = "PyAudio provides Python bindings for PortAudio, the cross-platform audio I/O library"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7c3152b432b96d6dc4a1cb35397da9ec"

PYPI_PACKAGE = "PyAudio"

SRC_URI[sha256sum] = "78dfff3879b4994d1f4fc6485646a57755c6ee3c19647a491f790a0895bd2f87"

inherit pypi setuptools3

SRC_URI += "file://0001-Remove-absolute-paths-into-build-machine-system-dirs.patch"

DEPENDS += "portaudio-v19"

RDEPENDS:${PN} += "portaudio-v19"
