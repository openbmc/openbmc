inherit setuptools
require python-grpcio.inc

RDEPENDS_${PN} += " python-enum34 \
                    python-futures \
"
