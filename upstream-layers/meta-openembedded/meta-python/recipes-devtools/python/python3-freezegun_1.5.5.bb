SUMMARY = "FreezeGun is a library that allows your Python tests to travel through time by mocking the datetime module."
HOMEPAGE = "https://github.com/spulec/freezegun"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=acf1d209bb6eddae4cbe6ffd6a0144fe"

SRC_URI[sha256sum] = "ac7742a6cc6c25a2c35e9292dfd554b897b517d2dec26891a2e8debf205cb94a"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
        python3-sqlite3 \
"

RDEPENDS:${PN} = "\
        python3-asyncio \
        python3-dateutil \
        python3-unittest \
"
