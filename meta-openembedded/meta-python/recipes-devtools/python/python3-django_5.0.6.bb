require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "ff1b61005004e476e0aeea47c7f79b85864c70124030e95146315396f1e7951f"

RDEPENDS:${PN} += "\
    python3-sqlparse \
    python3-asgiref \
"
