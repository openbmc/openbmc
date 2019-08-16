SUMMARY = "PIP is a tool for installing and managing Python packages"
HOMEPAGE = "https://pip.pypa.io/"
LICENSE = "MIT & LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8ba06d529c955048e5ddd7c45459eb2e"

SRC_URI[md5sum] = "2ba0a3b76d39ccd90ca22bfa82fc635f"
SRC_URI[sha256sum] = "e05103825871e210d50a44c7e448587b0ed99dd775d3ef586304c58f40224a53"

inherit pypi setuptools

# Since PIP is like CPAN for PERL we need to drag in all python modules to ensure everything works
RDEPENDS_${PN}_class-target = "python-modules python-distribute python-misc"

BBCLASSEXTEND = "native nativesdk"
