SUMMARY = "Python interface to the Raspberry Pi camera module"
DESCRIPTION = "This package provides a pure Python interface to the Raspberry Pi camera module for Python 2.7 (or above) or Python 3.2 (or above)."
HOMEPAGE = "https://github.com/waveform80/picamera" 

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4de8aab427192e4a8322a71375d20e21"

RDEPENDS:${PN} = "python3-numbers   \
                  python3-ctypes    \
                  python3-colorzero \
                  picamera-libs     \
"

SRC_URI = "git://git@github.com/waveform80/picamera.git;protocol=ssh;branch=master"
SRCREV = "7e4f1d379d698c44501fb84b886fadf3fc164b70"

S = "${WORKDIR}/git"

inherit setuptools3

COMPATIBLE_HOST = "null"
COMPATIBLE_HOST:rpi:libc-glibc = "(arm.*)-linux"
