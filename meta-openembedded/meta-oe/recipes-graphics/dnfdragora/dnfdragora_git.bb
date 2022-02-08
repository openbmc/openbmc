SUMMARY = "dnfdragora is a DNF frontend, based on rpmdragora from Mageia (originally rpmdrake) Perl code."
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504 \
                   "

SRC_URI = "git://github.com/manatools/dnfdragora.git;branch=master;protocol=https \
           file://0001-disable-build-manpages.patch \
           file://0001-Do-not-set-PYTHON_INSTALL_DIR-by-running-python.patch \
           file://0001-To-fix-error-when-do_package.patch \
           "

PV = "1.1.2+git${SRCPV}"
SRCREV = "19e123132cfd4efd860e5204261c3c228bfe80a8"

S = "${WORKDIR}/git"

inherit cmake gettext pkgconfig python3-dir python3native distutils3-base mime-xdg

DEPENDS += "dnf python3 "
#DEPENDS_class-nativesdk += "nativesdk-python3"

RDEPENDS_${PN}_class-target = " python3-core libyui libyui-ncurses "

# manpages generation requires http://www.sphinx-doc.org/
EXTRA_OECMAKE = " -DWITH_MAN=OFF -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DPYTHON_DESIRED=3"

BBCLASSEXTEND = "nativesdk"

FILES_${PN} = "${PYTHON_SITEPACKAGES_DIR}/ ${datadir}/ ${bindir}/ ${sysconfdir}/dnfdragora ${sysconfdir}/xdg"

PNBLACKLIST[dnfdragora] ?= "${@bb.utils.contains('PACKAGE_CLASSES', 'package_rpm', '', 'does not build correctly without package_rpm in PACKAGE_CLASSES', d)}"

