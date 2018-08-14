inherit setuptools
require python-aws-iot-device-sdk-python.inc

RDEPENDS_${PN}-examples += "${PYTHON_PN}-argparse"
