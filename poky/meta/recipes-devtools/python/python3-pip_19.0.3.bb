SUMMARY = "The PyPA recommended tool for installing Python packages"
HOMEPAGE = "https://pypi.python.org/pypi/pip"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8ba06d529c955048e5ddd7c45459eb2e"

DEPENDS += "python3 python3-setuptools-native"

SRC_URI[md5sum] = "1c5edb0924a0d7d79f3a2e3df05009b4"
SRC_URI[sha256sum] = "6e6f197a1abfb45118dbb878b5c859a0edbdd33fd250100bc015b67fded4b9f2"

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
