SUMMARY = "PIP is a tool for installing and managing Python packages"
HOMEPAGE = "https://pip.pypa.io/"
LICENSE = "MIT & LGPL-2.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8ba06d529c955048e5ddd7c45459eb2e"

SRC_URI[md5sum] = "a57da8b758cbf1a155cde6a7a4428ba7"
SRC_URI[sha256sum] = "324d234b8f6124846b4e390df255cacbe09ce22791c3b714aa1ea6e44a4f2861"

inherit pypi setuptools

# Since PIP is like CPAN for PERL we need to drag in all python modules to ensure everything works
RDEPENDS_${PN}_class-target = "python-modules python-distribute python-misc"

BBCLASSEXTEND = "native nativesdk"
