SUMMARY = "meld3 templating system used by Supervisor"
DESCRIPTION = "\
meld3 is an HTML/XML templating system for Python which keeps \
template markup and dynamic rendering logic separate from one \
another.  See http://www.entrian.com/PyMeld for a treatise on the \
benefits of this pattern."
HOMEPAGE = "https://github.com/supervisor/meld3"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9e7581cef5645475fcefebdc15ed7abf"

SRC_URI[md5sum] = "3ccc78cd79cffd63a751ad7684c02c91"
SRC_URI[sha256sum] = "f7b754a0fde7a4429b2ebe49409db240b5699385a572501bb0d5627d299f9558"

PYPI_PACKAGE = "meld3"
inherit pypi
