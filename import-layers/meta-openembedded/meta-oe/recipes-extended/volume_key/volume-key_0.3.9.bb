DESCRIPTION = "The volume_key project provides a libvolume_key, a library for manipulating \
storage volume encryption keys and storing them separately from volumes, and an \
associated command-line tool, named volume_key."
LICENSE = "GPLv2"
SECTION = "devel/lib"

HOMEPAGE = "https://pagure.io/volume_key"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://releases.pagure.org/volume_key/volume_key-${PV}.tar.xz \
           file://0001-explicitly-support-python3-by-pkg-config.patch \
"
SRC_URI[md5sum] = "a2d14931177c660e1f3ebbcf5f47d8e2"
SRC_URI[sha256sum] = "450a54fe9bf56acec6850c1e71371d3e4913c9ca1ef0cdc3a517b4b6910412a6"

SRCNAME = "volume_key"
S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit autotools python3native gettext

DEPENDS += " \
    util-linux \
    glib-2.0 \
    cryptsetup \
    nss \
    gpgme \
"

RDEPENDS_python3-${PN} += "${PN}"

PACKAGES += "python3-${PN}"
FILES_python3-${PN} = "${PYTHON_SITEPACKAGES_DIR}/*"

