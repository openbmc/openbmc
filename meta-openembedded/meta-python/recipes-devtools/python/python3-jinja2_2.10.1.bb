inherit pypi setuptools3
require python-jinja2.inc

RDEPENDS_${PN} += "${PYTHON_PN}-asyncio"
