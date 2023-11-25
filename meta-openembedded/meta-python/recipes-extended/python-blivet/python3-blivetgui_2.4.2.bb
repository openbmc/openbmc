DESCRIPTION = "GUI tool for storage configuration using blivet library"
HOMEPAGE = "https://github.com/rhinstaller/blivet-gui"
LICENSE = "GPL-2.0-or-later"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/storaged-project/blivet-gui.git;branch=main;protocol=https"
SRCREV = "9b6a83a7aef546ddacafcc656984c9bbed67a8ec"

S = "${WORKDIR}/git"

inherit features_check setuptools3
REQUIRED_DISTRO_FEATURES = "x11 systemd gobject-introspection-data"

PIP_INSTALL_PACKAGE = "blivet_gui"

RDEPENDS:${PN} = "python3-pygobject python3 \
                  python3-blivet gtk+3  \
                  python3-pid libreport \
"

FILES:${PN} += "${datadir}/*"
