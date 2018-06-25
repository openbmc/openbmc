SUMMARY = "PIP is a tool for installing and managing Python packages"
HOMEPAGE = "https://pip.pypa.io/"
LICENSE = "MIT & LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=25fba45109565f87de20bae85bc39452"

SRC_URI[md5sum] = "b15b33f9aad61f88d0f8c866d16c55d8"
SRC_URI[sha256sum] = "7bf48f9a693be1d58f49f7af7e0ae9fe29fd671cde8a55e6edca3581c4ef5796"

inherit pypi setuptools

# Since PIP is like CPAN for PERL we need to drag in all python modules to ensure everything works
RDEPENDS_${PN}_class-target = "python-modules python-distribute python-misc"

BBCLASSEXTEND = "native nativesdk"
