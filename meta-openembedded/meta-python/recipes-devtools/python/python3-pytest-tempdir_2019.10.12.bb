require python-pytest-tempdir.inc

inherit setuptools3

RDEPENDS_${PN} += " \
    python3-pytest \
    "
