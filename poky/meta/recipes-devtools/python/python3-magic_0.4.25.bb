SUMMARY = "File type identification using libmagic"
DESCRIPTION = "This module uses ctypes to access the libmagic file type \
    identification library. It makes use of the local magic database and supports \
    both textual and MIME-type output."
HOMEPAGE = "http://github.com/ahupp/python-magic"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61495c152d794e6be5799a9edca149e3"

PYPI_PACKAGE = "python-magic"

inherit pypi setuptools3

SRC_URI[sha256sum] = "21f5f542aa0330f5c8a64442528542f6215c8e18d2466b399b0d9d39356d83fc"

RDEPENDS:${PN} += "file \
                   ${PYTHON_PN}-ctypes \
                   ${PYTHON_PN}-io \
                   ${PYTHON_PN}-logging \
                   ${PYTHON_PN}-shell"

BBCLASSEXTEND = "native"
