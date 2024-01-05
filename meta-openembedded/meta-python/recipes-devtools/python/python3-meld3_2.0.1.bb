SUMMARY = "meld3 templating system used by Supervisor"
DESCRIPTION = "\
meld3 is an HTML/XML templating system for Python which keeps \
template markup and dynamic rendering logic separate from one \
another.  See http://www.entrian.com/PyMeld for a treatise on the \
benefits of this pattern."
HOMEPAGE = "https://github.com/supervisor/meld3"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9e7581cef5645475fcefebdc15ed7abf"

SRC_URI[md5sum] = "1add16caefc9e6b82ef4f40532cb4918"
SRC_URI[sha256sum] = "3ea266994f1aa83507679a67b493b852c232a7905e29440a6b868558cad5e775"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-cgitb \
    python3-xml \
"
