SUMMARY = "PyAudio provides Python bindings for PortAudio, the cross-platform audio I/O library"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=7c3152b432b96d6dc4a1cb35397da9ec"

PYPI_PACKAGE = "PyAudio"

SRC_URI[md5sum] = "41199ffd2abbdaf1ce6b88cf8af48cc5"
SRC_URI[sha256sum] = "26bccc81e4243d1c0ff5487e6b481de6329fcd65c79365c267cef38f363a2b56"

inherit pypi setuptools3

SRC_URI += "file://0001-Remove-absolute-paths-into-build-machine-system-dirs.patch"

DEPENDS += "portaudio-v19"

RDEPENDS:${PN} += "portaudio-v19"
