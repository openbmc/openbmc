inherit pypi setuptools
require python-importlib-metadata.inc
RDEPENDS_${PN} += "python-configparser \
                   python-contextlib2 \
                   python-pathlib2 \
                   python-compression \
"
