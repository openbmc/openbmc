SUMMARY = "The PyPA recommended tool for installing Python packages"
HOMEPAGE = "https://pypi.org/project/pip"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=63ec52baf95163b597008bb46db68030"

inherit pypi python_setuptools_build_meta

SRC_URI += "file://0001-change-shebang-to-python3.patch"
SRC_URI += "file://no_shebang_mangling.patch"
SRC_URI += "file://reproducible.patch"

SRC_URI[sha256sum] = "f29d589df8c8ab99c060e68ad294c4a9ed896624f6368c5349d70aa581b333d0"

do_install:append() {
    rm -f ${D}/${bindir}/pip
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

# This used to use the bootstrap install which didn't compile. Until we bump the
# tmpdir version we can't compile the native otherwise the sysroot unpack fails
INSTALL_WHEEL_COMPILE_BYTECODE:class-native = "--no-compile-bytecode"
