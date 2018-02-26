DESCRIPTION = "langtable is used to guess reasonable defaults for locale,\
keyboard, territory"
HOMEPAGE = "https://github.com/mike-fabian/langtable/"
LICENSE = "GPLv3+"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "3f001eef027ba69ef2fdb35c670b7da26b79b5e2"
PV = "0.0.37+git${SRCPV}"
SRC_URI = "git://github.com/mike-fabian/langtable.git;branch=master \
"

inherit setuptools3 python3native

DISTUTILS_INSTALL_ARGS = "--prefix=${D}/${prefix} \
    --install-data=${D}/${datadir}/langtable"

FILES_${PN} += "${datadir}/*"
