SUMMARY =  "djangorestframework"
DESCRIPTION = "pip3 install djangorestframework"
HOMEPAGE = "https://pypi.python.org/pypi/djangorestframework"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=7879a5a716147a784f7e524c9cf103c1"

SRC_URI[sha256sum] = "446a9b352e7eff630421ab3f2328bd2401b109a9470afa4a31189994911ed030"


inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += "\
    python3-django \
"
