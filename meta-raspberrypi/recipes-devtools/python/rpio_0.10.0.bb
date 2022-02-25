DESCRIPTION = "Advanced GPIO for the Raspberry Pi. Extends RPi.GPIO with PWM, \
GPIO interrups, TCP socket interrupts, command line tools and more"
HOMEPAGE = "https://github.com/metachris/RPIO"
SECTION = "devel/python"
LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://README.rst;beginline=41;endline=53;md5=d5d95d7486a4d98c999675c23196b25a"

PYPI_PACKAGE = "RPIO"
inherit pypi

SRC_URI += "file://0001-include-sys-types.h-explicitly-for-getting-caddr_t-d.patch"

inherit setuptools3

COMPATIBLE_MACHINE = "^rpi$"

RDEPENDS:${PN} = "\
    python3-logging \
    python3-threading \
"

SRC_URI[md5sum] = "cefc45422833dcafcd59b78dffc540f4"
SRC_URI[sha256sum] = "b89f75dec9de354681209ebfaedfe22b7c178aacd91a604a7bd6d92024e4cf7e"
