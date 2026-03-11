SUMMARY = "FreezeGun is a library that allows your Python tests to travel through time by mocking the datetime module."
HOMEPAGE = "https://github.com/spulec/freezegun"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=acf1d209bb6eddae4cbe6ffd6a0144fe"

SRC_URI[sha256sum] = "d7c6204e33a50affd7c7aa284f4f92e04e96f72d63313b89ceaaf60d9c64bc5e"

inherit pypi python_setuptools_build_meta ptest-python-pytest

RDEPENDS:${PN}-ptest += " \
        python3-sqlite3 \
"

RDEPENDS:${PN} = "\
        python3-asyncio \
        python3-dateutil \
        python3-unittest \
"
