SUMMARY = "dnfdragora is a DNF frontend, based on rpmdragora from Mageia (originally rpmdrake) Perl code."
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504 \
                   "

SRC_URI = "git://github.com/manatools/dnfdragora.git;branch=master;protocol=https \
           file://0001-disable-build-manpages.patch \
           file://0001-Do-not-set-PYTHON_INSTALL_DIR-by-running-python.patch \
           file://0001-To-fix-error-when-do_package.patch \
           "

SRCREV = "5cbbc07c9d015af284a424a172a379b385f05b6f"

S = "${WORKDIR}/git"

inherit cmake gettext pkgconfig python3-dir python3native setuptools3-base mime-xdg

DEPENDS += "dnf python3 "
#DEPENDS:class-nativesdk += "nativesdk-python3"

RDEPENDS:${PN}:class-target = " python3-core libyui libyui-ncurses "

# manpages generation requires http://www.sphinx-doc.org/
EXTRA_OECMAKE = " -DWITH_MAN=OFF -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DPYTHON_DESIRED=3"

BBCLASSEXTEND = "nativesdk"

FILES:${PN} = "${PYTHON_SITEPACKAGES_DIR}/ ${datadir}/ ${bindir}/ ${sysconfdir}/dnfdragora ${sysconfdir}/xdg"

PNBLACKLIST[dnfdragora] ?= "${@bb.utils.contains('PACKAGE_CLASSES', 'package_rpm', '', 'does not build correctly without package_rpm in PACKAGE_CLASSES', d)}"

