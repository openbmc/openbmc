SUMMARY = "Rate Limiting extension for Flask"
DESCRIPTION = "Flask-Limiter adds rate limiting to Flask applications."
HOMEPAGE = "https://github.com/alisaifee/flask-limiter"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2455d5e574bc0fc489411ca45766ac78"

SRC_URI[sha256sum] = "ca11608fc7eec43dcea606964ca07c3bd4ec1ae89043a0f67f717899a4f48106"

PYPI_PACKAGE = "flask_limiter"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-vcs-native"

RDEPENDS:${PN} += " \
	python3-flask \
	python3-limits \
	python3-ordered-set \
	python3-werkzeug \
"
