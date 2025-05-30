SUMMARY = "A microframework based on Werkzeug, Jinja2 and good intentions"
DESCRIPTION = "\
Flask is a microframework for Python based on Werkzeug, Jinja 2 and good \
intentions. And before you ask: It’s BSD licensed!"
HOMEPAGE = "https://github.com/pallets/flask"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=ffeffa59c90c9c4a033c7574f8f3fb75"

SRC_URI[sha256sum] = "284c7b8f2f58cb737f0cf1c30fd7eaf0ccfcde196099d24ecede3fc2005aa59e"

inherit pypi python_setuptools_build_meta ptest-python-pytest

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
