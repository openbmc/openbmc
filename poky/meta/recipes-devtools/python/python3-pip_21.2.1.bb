SUMMARY = "The PyPA recommended tool for installing Python packages"
HOMEPAGE = "https://pypi.org/project/pip"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c4fa2b50f55649f43060fa04b0919b9b"

DEPENDS += "python3 python3-setuptools-native"

inherit pypi distutils3

SRC_URI += "file://0001-change-shebang-to-python3.patch"

SRC_URI[sha256sum] = "303a82aaa24cdc01f7ebbd1afc7d1b871a4aa0a88bb5bedef1fa86a3ee44ca0a"

do_install:append() {
    # Install as pip3 and leave pip2 as default
    rm ${D}/${bindir}/pip
}

RDEPENDS:${PN} = "\
  python3-compile \
  python3-io \
  python3-html \
  python3-json \
  python3-multiprocessing \
  python3-netserver \
  python3-setuptools \
  python3-unixadmin \
  python3-xmlrpc \
  python3-pickle \
"

BBCLASSEXTEND = "native nativesdk"
