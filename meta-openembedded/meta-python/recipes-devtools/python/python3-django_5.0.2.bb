require python-django.inc
inherit setuptools3

SRC_URI[sha256sum] = "b5bb1d11b2518a5f91372a282f24662f58f66749666b0a286ab057029f728080"

RDEPENDS:${PN} += "\
    python3-sqlparse \
    python3-asgiref \
"
