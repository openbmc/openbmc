SUMMARY = "The PyPA recommended tool for installing Python packages"
HOMEPAGE = "https://pypi.python.org/pypi/pip"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8ba06d529c955048e5ddd7c45459eb2e"

DEPENDS += "python3 python3-setuptools-native"

SRC_URI[md5sum] = "7d42ba49b809604f0df3d55df1c3fd86"
SRC_URI[sha256sum] = "7db0c8ea4c7ea51c8049640e8e6e7fde949de672bfa4949920675563a5a6967f"

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
  python3-pickle \
"

BBCLASSEXTEND = "native nativesdk"
