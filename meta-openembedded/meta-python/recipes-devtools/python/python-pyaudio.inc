SUMMARY = "PyAudio provides Python bindings for PortAudio, the cross-platform audio I/O library"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README;md5=288793c2b9b05bd67abbd2a8f5d144f7"

PYPI_PACKAGE = "PyAudio"

SRC_URI[md5sum] = "7e4c88139284033f67b4336c74eda3b8"
SRC_URI[sha256sum] = "93bfde30e0b64e63a46f2fd77e85c41fd51182a4a3413d9edfaf9ffaa26efb74"

DEPENDS += "portaudio-v19"

RDEPENDS_${PN} += "portaudio-v19"
