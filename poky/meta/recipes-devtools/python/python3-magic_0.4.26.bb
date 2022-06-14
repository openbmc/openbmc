SUMMARY = "File type identification using libmagic"
DESCRIPTION = "This module uses ctypes to access the libmagic file type \
    identification library. It makes use of the local magic database and supports \
    both textual and MIME-type output."
HOMEPAGE = "http://github.com/ahupp/python-magic"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61495c152d794e6be5799a9edca149e3"

PYPI_PACKAGE = "python-magic"

inherit pypi setuptools3

SRC_URI[sha256sum] = "8262c13001f904ad5b724d38b5e5b5f17ec0450ae249def398a62e4e33108a50"

RDEPENDS:${PN} += "file \
                   ${PYTHON_PN}-ctypes \
                   ${PYTHON_PN}-io \
                   ${PYTHON_PN}-logging \
                   ${PYTHON_PN}-shell"

BBCLASSEXTEND = "native"
