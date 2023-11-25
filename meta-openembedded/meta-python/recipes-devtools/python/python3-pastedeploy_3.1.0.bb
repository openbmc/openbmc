SUMMARY = "Load, configure, and compose WSGI applications and servers"
HOMEPAGE = "https://pylonsproject.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://license.txt;md5=1798f29d55080c60365e6283cb49779c"

inherit pypi
PYPI_PACKAGE = "PasteDeploy"

SRC_URI[sha256sum] = "9ddbaf152f8095438a9fe81f82c78a6714b92ae8e066bed418b6a7ff6a095a95"

S = "${WORKDIR}/PasteDeploy-${PV}"

inherit setuptools3

# Uncomment this line to enable all the optional features.
#PACKAGECONFIG ?= "paste docs"
PACKAGECONFIG[paste] = ",,,python3-paste"
PACKAGECONFIG[docs] = ",,,python3-sphinx python3-pylons-sphinx-themes"

DEPENDS= "python3 python3-setuptools-scm-native python3-pytest-runner-native"
RDEPENDS:${PN} += "python3-core  python3-misc python3-netclient python3-pkgutil python3-setuptools python3-threading python3-core"
