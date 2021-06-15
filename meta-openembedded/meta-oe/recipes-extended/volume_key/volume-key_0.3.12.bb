DESCRIPTION = "The volume_key project provides a libvolume_key, a library for manipulating \
storage volume encryption keys and storing them separately from volumes, and an \
associated command-line tool, named volume_key."
LICENSE = "GPLv2"
SECTION = "devel/lib"

HOMEPAGE = "https://pagure.io/volume_key"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://releases.pagure.org/volume_key/volume_key-${PV}.tar.xz \
"
SRC_URI[md5sum] = "200591290173c3ea71528411838f9080"
SRC_URI[sha256sum] = "6ca3748fc1dad22c450bbf6601d4e706cb11c5e662d11bb4aeb473a9cd77309b"

SRCNAME = "volume_key"
S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit autotools python3native gettext

DEPENDS += " \
    util-linux \
    glib-2.0 \
    cryptsetup \
    nss \
    gpgme \
    swig-native \
"

PACKAGECONFIG ??= "python3"
PACKAGECONFIG[python3] = "--with-python3,--without-python3,python3,python3"

EXTRA_OECONF = "--without-python"

RDEPENDS_python3-${BPN} += "${PN}"

PACKAGES += "python3-${BPN}"
FILES_python3-${BPN} = "${PYTHON_SITEPACKAGES_DIR}/*"
