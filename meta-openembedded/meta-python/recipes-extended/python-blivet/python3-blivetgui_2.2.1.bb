DESCRIPTION = "GUI tool for storage configuration using blivet library"
HOMEPAGE = "https://github.com/rhinstaller/blivet-gui"
LICENSE = "GPLv2+"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "adb6bd69fb3cfa709265db66ddcee04c0b5e070f"
SRC_URI = "git://github.com/storaged-project/blivet-gui.git;branch=master;protocol=https"

inherit features_check
REQUIRED_DISTRO_FEATURES = "x11 systemd"

inherit setuptools3 python3native

RDEPENDS:${PN} = "python3-pygobject python3 \
                  python3-blivet gtk+3  \
                  python3-pid libreport \
"

FILES:${PN} += " \
    ${datadir}/* \
    "
