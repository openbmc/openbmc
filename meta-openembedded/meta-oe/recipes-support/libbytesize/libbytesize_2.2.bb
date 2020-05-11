DESCRIPTION = "The goal of this project is to provide a tiny library that would \
facilitate the common operations with sizes in bytes."
HOMEPAGE = "https://github.com/rhinstaller/libbytesize"
LICENSE = "LGPLv2+"
SECTION = "devel/lib"

LIC_FILES_CHKSUM = "file://LICENSE;md5=c07cb499d259452f324bb90c3067d85c"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "e64e752a28a4a41b0a43cba3bedf9571c22af807"
SRC_URI = "git://github.com/rhinstaller/libbytesize;branch=master"

inherit gettext autotools python3native

DEPENDS += " \
    libpcre2 \
    gmp \
    mpfr \
    gettext-native \
"

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}/bytesize"

PACKAGECONFIG ??= "python3"
PACKAGECONFIG[python3] = "--with-python3, --without-python3,,python3-core"

EXTRA_OECONF = "--without-gtk-doc"
EXTRA_OEMAKE = "py3libdir=${PYTHON_SITEPACKAGES_DIR}"
