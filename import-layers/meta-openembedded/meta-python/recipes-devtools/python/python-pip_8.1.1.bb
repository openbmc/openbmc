SUMMARY = "PIP is a tool for installing and managing Python packages"
LICENSE = "MIT & LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=25fba45109565f87de20bae85bc39452"

SRC_URI[md5sum] = "6b86f11841e89c8241d689956ba99ed7"
SRC_URI[sha256sum] = "3e78d3066aaeb633d185a57afdccf700aa2e660436b4af618bcb6ff0fa511798"

inherit pypi setuptools

# Since PIP is like CPAN for PERL we need to drag in all python modules to ensure everything works
RDEPENDS_${PN} = "python-modules python-distribute"
