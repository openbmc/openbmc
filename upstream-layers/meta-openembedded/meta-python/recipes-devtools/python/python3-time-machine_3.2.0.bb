SUMMARY = "Travel through time in your tests."
HOMEPAGE = "https://github.com/adamchainz/time-machine"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fb9b93a440d3ef2fb6eeebfa59384c53"

SRCREV = "1b7ac45defb26c72076dc202aba46967c8c28fa9"
PYPI_SRC_URI = "git://github.com/adamchainz/time-machine;protocol=https;branch=main;tag=${PV};destsuffix=time_machine-${PV}"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PYPI_PACKAGE = "time_machine"

RDEPENDS:${PN} += "\
    python3-tzdata \
    python3-unittest \
    python3-zoneinfo \
    "
do_install_ptest:append() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
    # test_destination_datetime_tzinfo_non_zoneinfo builds an aware datetime
    # from dateutil's tz.gettz("America/Chicago") and expects the CST (-6h)
    # offset at the 1970 epoch. OE builds /usr/share/zoneinfo in "slim" format
    # (zic -b slim); dateutil's tzfile parser mishandles the pre-first-transition
    # instant of a slim tzfile and returns a 0 (UTC) offset, so the epoch maps to
    # 0 instead of 21600. Python's own zoneinfo reads the same file correctly, so
    # this is a dateutil/slim-tzdata limitation, not a time_machine bug.
    #
    # test_marker_function/_and_fixture/_class/_module use the "testdir" fixture,
    # provided by pytest's builtin pytester plugin. That plugin isn't autoloaded;
    # upstream's pyproject.toml sets [tool.pytest.ini_options] addopts = "-p
    # pytester", but that file isn't shipped into the ptest tree, so the fixture
    # is unavailable and those 4 tests error out. Pass -p pytester directly.
    sed -i -e "/--automake/ s|\$| -p pytester --deselect tests/test_time_machine.py::test_destination_datetime_tzinfo_non_zoneinfo|" \
        ${D}${PTEST_PATH}/run-ptest
}

RDEPENDS:${PN}-ptest += "\
    python3-dateutil \
    python3-dateutil-zoneinfo \
    python3-freezegun \
    python3-tokenize-rt \
    tzdata \
"
