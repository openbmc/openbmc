require python-pytest-tempdir.inc

inherit setuptools

RDEPENDS_${PN} += " \
    python-pytest \
    "
