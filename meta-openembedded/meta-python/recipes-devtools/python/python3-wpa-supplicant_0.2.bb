SUMMARY = "Python interface to the wpa_supplicant D-Bus interface"
HOMEPAGE = "https://pypi.org/project/wpa_supplicant/"
SECTION = "devel/python"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://README.md;beginline=171;endline=199;md5=462586bcbebd12f5d0ac443be0ed3d91"

SRC_URI[md5sum] = "c21c7841f5e03ff709a0564b2e094010"
SRC_URI[sha256sum] = "3ad0f40a696763bb0f4d4dec5b51f6b53ccfeb7c16ebb5897349303045f94776"

SRC_URI += "file://0001-cli-drop-the-second-argument-from-click.argument-dec.patch"

PYPI_PACKAGE = "wpa_supplicant"
UPSTREAM_CHECK_URI = "https://pypi.python.org/pypi/wpa_supplicant"
UPSTREAM_CHECK_REGEX = "/wpa_supplicant/(?P<pver>(\d+[\.\-_]*)+)"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-twisted python3-click python3-txdbus"
