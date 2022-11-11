SUMMARY = "Load, configure, and compose WSGI applications and servers"
HOMEPAGE = "https://pylonsproject.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://license.txt;md5=1798f29d55080c60365e6283cb49779c"

inherit pypi
PYPI_PACKAGE = "PasteDeploy"

SRC_URI[sha256sum] = "5f4b4d5fddd39b8947ea727161e366bf55b90efc60a4d1dd7976b9031d0b4e5f"

S = "${WORKDIR}/PasteDeploy-${PV}"

inherit setuptools3

# Uncomment this line to enable all the optional features.
#PACKAGECONFIG ?= "paste docs"
PACKAGECONFIG[paste] = ",,,python3-paste"
PACKAGECONFIG[docs] = ",,,python3-sphinx python3-pylons-sphinx-themes"

DEPENDS= "python3 python3-setuptools-scm-native python3-pytest-runner-native"
RDEPENDS:${PN} += "python3-core  python3-misc python3-netclient python3-pkgutil python3-setuptools python3-threading python3-core"
