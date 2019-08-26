SUMMARY = "The PyPA recommended tool for installing Python packages"
HOMEPAGE = "https://pypi.python.org/pypi/pip"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8ba06d529c955048e5ddd7c45459eb2e"

DEPENDS += "python3 python3-setuptools-native"

SRC_URI[md5sum] = "2ba0a3b76d39ccd90ca22bfa82fc635f"
SRC_URI[sha256sum] = "e05103825871e210d50a44c7e448587b0ed99dd775d3ef586304c58f40224a53"

inherit pypi distutils3

do_install_append() {
    # Install as pip3 and leave pip2 as default
    rm ${D}/${bindir}/pip
}

RDEPENDS_${PN} = "\
  python3-compile \
  python3-io \
  python3-html \
  python3-json \
  python3-netserver \
  python3-setuptools \
  python3-unixadmin \
  python3-xmlrpc \
"

BBCLASSEXTEND = "native nativesdk"
