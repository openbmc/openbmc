DESCRIPTION = "Advanced GPIO for the Raspberry Pi. Extends RPi.GPIO with PWM, \
GPIO interrups, TCP socket interrupts, command line tools and more"
HOMEPAGE = "https://github.com/metachris/RPIO"
SECTION = "devel/python"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=bb3ca60759f3202f1ae42e3519cd06bc"

SRC_URI = "\
    git://github.com/metachris/RPIO.git;protocol=https;branch=master \
    "
SRCREV = "be1942a69b2592ddacd9dc833d2668a19aafd8d2"

inherit setuptools3

COMPATIBLE_MACHINE = "^rpi$"

RDEPENDS:${PN} = "\
    python3-logging \
    python3-threading \
"

SRC_URI[md5sum] = "cefc45422833dcafcd59b78dffc540f4"
SRC_URI[sha256sum] = "b89f75dec9de354681209ebfaedfe22b7c178aacd91a604a7bd6d92024e4cf7e"
