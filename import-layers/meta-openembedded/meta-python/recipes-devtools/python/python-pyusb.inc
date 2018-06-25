SUMMARY = "PyUSB provides USB access on the Python language"
HOMEPAGE = "http://pyusb.sourceforge.net/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=51691ed11cc2c7ae93e383f60ed49b0f"
DEPENDS += "libusb1"

SRC_URI[md5sum] = "862b56452c64948c787ad8ef9498590b"
SRC_URI[sha256sum] = "4e9b72cc4a4205ca64fbf1f3fff39a335512166c151ad103e55c8223ac147362"

inherit pypi
