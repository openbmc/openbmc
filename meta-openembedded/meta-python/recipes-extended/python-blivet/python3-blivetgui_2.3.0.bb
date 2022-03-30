DESCRIPTION = "GUI tool for storage configuration using blivet library"
HOMEPAGE = "https://github.com/rhinstaller/blivet-gui"
LICENSE = "GPL-2.0-or-later"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "42512ee48494cee71febf04078d9774f0146a085"
SRC_URI = "git://github.com/storaged-project/blivet-gui.git;branch=master;protocol=https \
           file://0001-Use-setuptools-instead-of-distutils-in-setup.py.patch \
          "

inherit features_check
REQUIRED_DISTRO_FEATURES = "x11 systemd"

inherit setuptools3_legacy python3native

PIP_INSTALL_PACKAGE = "blivet_gui"

RDEPENDS:${PN} = "python3-pygobject python3 \
                  python3-blivet gtk+3  \
                  python3-pid libreport \
"

FILES:${PN} += " \
    ${datadir}/* \
    "
