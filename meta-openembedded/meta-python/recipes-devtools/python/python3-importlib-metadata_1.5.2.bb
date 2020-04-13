inherit pypi setuptools3
require python-importlib-metadata.inc

RDEPENDS_${PN}_append_class-target = " python3-misc"
RDEPENDS_${PN}_append_class-nativesdk = " python3-misc"
