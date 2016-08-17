SUMMARY = "A Python Mocking and Patching Library for Testing"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=de9dfbf780446b18aab11f00baaf5b7e"

SRC_URI[md5sum] = "73ee8a4afb3ff4da1b4afa287f39fdeb"
SRC_URI[sha256sum] = "1e247dbecc6ce057299eb7ee019ad68314bb93152e81d9a6110d35f4d5eca0f6"

inherit pypi setuptools

RDEPENDS_${PN} += " \
    python-prettytable \
    python-cmd2 \
    python-pyparsing \
    python-mccabe \
    python-pep8 \
    python-pyflakes"
