SUMMARY = "Load, configure, and compose WSGI applications and servers"
HOMEPAGE = "https://pylonsproject.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://license.txt;md5=1798f29d55080c60365e6283cb49779c"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "PasteDeploy"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

SRC_URI[sha256sum] = "9ddbaf152f8095438a9fe81f82c78a6714b92ae8e066bed418b6a7ff6a095a95"

# Uncomment this line to enable all the optional features.
#PACKAGECONFIG ?= "paste docs"
PACKAGECONFIG[paste] = ",,,python3-paste"
PACKAGECONFIG[docs] = ",,,python3-sphinx python3-pylons-sphinx-themes"

RDEPENDS:${PN} += " \
	python3-core \
	python3-misc \
	python3-netclient \
	python3-pkgutil \
	python3-setuptools \
	python3-threading \
"
