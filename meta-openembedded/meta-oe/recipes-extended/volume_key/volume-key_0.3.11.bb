DESCRIPTION = "The volume_key project provides a libvolume_key, a library for manipulating \
storage volume encryption keys and storing them separately from volumes, and an \
associated command-line tool, named volume_key."
LICENSE = "GPLv2"
SECTION = "devel/lib"

HOMEPAGE = "https://pagure.io/volume_key"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "https://releases.pagure.org/volume_key/volume_key-${PV}.tar.xz \
"
SRC_URI[md5sum] = "30df56c7743eb7c965293b3d61194232"
SRC_URI[sha256sum] = "e6b279c25ae477b555f938db2e41818f90c8cde942b0eec92f70b6c772095f6d"

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

RDEPENDS_python3-${PN} += "${PN}"

PACKAGES += "python3-${PN}"
FILES_python3-${PN} = "${PYTHON_SITEPACKAGES_DIR}/*"

