DESCRIPTION = "An extension for the Flask microframework that adds Sijax support."
HOMEPAGE = "https://github.com/spantaleev/flask-sijax"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=266adc7b911b7c84b837bf77196e1ba6"

SRC_URI[sha256sum] = "fb2bf2d4f75408185102195055d75549fee8d9c9e954dca2427186925cdc429f"

PYPI_PACKAGE = "Flask-Sijax"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-flask \
    python3-sijax \
    python3-werkzeug \
"
