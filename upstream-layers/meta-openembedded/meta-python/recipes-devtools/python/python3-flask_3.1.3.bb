SUMMARY = "A microframework based on Werkzeug, Jinja2 and good intentions"
DESCRIPTION = "\
Flask is a microframework for Python based on Werkzeug, Jinja 2 and good \
intentions. And before you ask: It is BSD licensed!"
HOMEPAGE = "https://github.com/pallets/flask"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[sha256sum] = "0ef0e52b8a9cd932855379197dd8f94047b359ca0a78695144304cb45f87c9eb"

CVE_PRODUCT = "palletsprojects:flask"

inherit pypi python_flit_core ptest-python-pytest

CLEANBROKEN = "1"

RDEPENDS:${PN} = " \
    python3-blinker \
    python3-click \
    python3-importlib-metadata \
    python3-itsdangerous \
    python3-jinja2 \
    python3-werkzeug \
"

RDEPENDS:${PN}-ptest += "\
    python3-asgiref \
"

# tests/test_cli.py and tests/conftest.py use pytest's monkeypatch "not set"
# sentinel, renamed from lowercase 'notset' to 'NOTSET' in pytest 9.1
do_install_ptest:append() {
    sed -i 's/from _pytest.monkeypatch import notset/from _pytest.monkeypatch import NOTSET as notset/' \
        ${D}${PTEST_PATH}/tests/test_cli.py
    sed -i 's/monkeypatch\.notset/monkeypatch.NOTSET/g' \
        ${D}${PTEST_PATH}/tests/conftest.py
    # werkzeug 3.1.x get_host() no longer raises SecurityError for a Host
    # header with invalid/non-printable characters when no trusted_hosts is
    # configured (flask's default) - it silently returns "" and the request
    # routes to a 404 instead of the 400 this test was written to expect
    # against older werkzeug. Version-skew, not a flask bug.
    sed -i '/^def test_bad_environ_raises_bad_request/i @pytest.mark.skip(reason="werkzeug 3.1.x get_host returns empty string for invalid Host chars -> 404 not 400")' \
        ${D}${PTEST_PATH}/tests/test_reqctx.py
}
