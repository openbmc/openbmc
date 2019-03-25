DESCRIPTION = "GUI tool for storage configuration using blivet library"
HOMEPAGE = "https://github.com/rhinstaller/blivet-gui"
LICENSE = "GPLv2+"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "a4fd427ee2acc5a8f5fb030bf7816917cee63bd8"
SRC_URI = "git://github.com/rhinstaller/blivet-gui;branch=master \
    file://0001-Set-_supported_filesystems-in-BlivetGUIAnaconda-init.patch \
"

inherit distro_features_check
REQUIRED_DISTRO_FEATURES = "x11 systemd"

inherit setuptools3 python3native

RDEPENDS_${PN} = "python3-pygobject python3 \
                  python3-blivet gtk+3  \
                  python3-pid libreport \
"

FILES_${PN} += " \
    ${datadir}/* \
    "
