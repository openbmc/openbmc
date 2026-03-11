SUMMARY = "Convert HTML to Markdown-formatted text"
HOMEPAGE = "https://github.com/Alir3z4/html2text"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI[sha256sum] = "948a645f8f0bc3abe7fd587019a2197a12436cd73d0d4908af95bfc8da337588"

inherit pypi python_setuptools_build_meta ptest-python-pytest

PTEST_PYTEST_DIR = "test"

DEPENDS += " \
	python3-setuptools-scm-native \
	python3-toml-native \
"

RDEPENDS:${PN} += "python3-html"

BBCLASSEXTEND = "native nativesdk"
