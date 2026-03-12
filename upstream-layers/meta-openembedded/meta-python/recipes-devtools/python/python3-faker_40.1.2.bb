SUMMARY = "Faker is a Python package that generates fake data for you."
DESCRIPTION = "Faker is a Python package that generates fake data for you. \
Whether you need to bootstrap your database, create good-looking XML documents, \
fill-in your persistence to stress test it, or anonymize data taken from a \
production service, Faker is for you."
HOMEPAGE = "https://github.com/joke2k/faker"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=53360c4126f7d03b63cb79b0dab9e9e1"

SRC_URI[sha256sum] = "b76a68163aa5f171d260fc24827a8349bc1db672f6a665359e8d0095e8135d30"

inherit pypi setuptools3 ptest-python-pytest

SRC_URI += "file://pytest.ini"

PACKAGECONFIG ?= "tzdata"
PACKAGECONFIG[tzdata] = ",,,python3-tzdata"

RDEPENDS:${PN} += "\
    python3-compression \
    python3-core \
    python3-crypt \
    python3-datetime \
    python3-image \
    python3-json \
    python3-logging \
    python3-math \
    python3-netclient \
    python3-numbers \
    python3-pickle \
    python3-pytest \
    python3-stringold \
    python3-unittest \
    python3-xml \
    python3-zoneinfo \
    "

RDEPENDS:${PN}-ptest += "\
    python3-freezegun \
    python3-validators \
"

do_install_ptest:append() {
    install ${UNPACKDIR}/pytest.ini ${D}${PTEST_PATH}/
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

