SUMMARY = "Recipe to embedded the Python PiP Package pylddwrap"
HOMEPAGE = "https://pypi.org/project/pylddwrap"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=48fd6c978d39a38b3a04f45a1456d0fa"

inherit pypi setuptools3
PYPI_PACKAGE = "pylddwrap"
SRC_URI[sha256sum] = "a70437fea7bca647c0e98161e1006ef49970267999c571b499760f1c43c6ba10"

PR = "r0"

RDEPENDS:${PN} += "python3-icontract"

BBCLASSEXTEND = "native nativesdk"

do_install:append() {
    # similarly to https://gitlab.com/akuster/meta-security/-/commit/0fd8e0f8cae612010bafecbff77ed9bb6f647a2d#4e154e295e639fd6c298ca644c75291eb99e0a57_0_16
    # but delete it from prefix and delete requirements.txt as well.
    # ERROR: QA Issue: python3-pylddwrap: Files/directories were installed but not shipped in any package:
    # /usr/README.rst
    # /usr/requirements.txt
    # /usr/LICENSE
    # Please set FILES such that these items are packaged. Alternatively if they are unneeded, avoid installing them or delete them within do_install.
    # python3-pylddwrap: 3 installed and not shipped files. [installed-vs-shipped]
    rm -f ${D}${prefix}/README.rst ${D}${prefix}/requirements.txt ${D}${prefix}/LICENSE
}
