SUMMARY = "Platform detection for use by libraries like Adafruit-Blinka."
HOMEPAGE = "https://github.com/adafruit/Adafruit_Python_PlatformDetect"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fccd531dce4b989c05173925f0bbb76c"

SRC_URI = "git://github.com/adafruit/Adafruit_Python_PlatformDetect.git;branch=main;protocol=https"
SRCREV = "e0fe1b012898fa824944d6805ca74be0fa027968"

S = "${WORKDIR}/git"

inherit setuptools3

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "python3-core"
