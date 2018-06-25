inherit setuptools
require python-paho-mqtt.inc

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-subprocess \
"
