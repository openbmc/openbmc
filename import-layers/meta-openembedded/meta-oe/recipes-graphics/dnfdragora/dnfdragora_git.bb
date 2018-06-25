SUMMARY = "dnfdragora is a DNF frontend, based on rpmdragora from Mageia (originally rpmdrake) Perl code."
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504 \
                   "

SRC_URI = "git://github.com/manatools/dnfdragora.git \
           file://0001-disable-build-manpages.patch \
           file://0001-Do-not-set-PYTHON_INSTALL_DIR-by-running-python.patch \
           file://0001-To-fix-error-when-do_package.patch \
           file://0001-Run-python-scripts-using-env.patch \
           "

PV = "1.0.1+git${SRCPV}"
SRCREV = "4fef4ce889b8e4fa03191d414f63bfd50796152a"

S = "${WORKDIR}/git"

inherit cmake gettext pkgconfig python3-dir python3native distutils3-base

DEPENDS += "dnf python3 "
#DEPENDS_class-nativesdk += "nativesdk-python3"

RDEPENDS_${PN}_class-target = " python3-core libyui libyui-ncurses "

# manpages generation requires http://www.sphinx-doc.org/
EXTRA_OECMAKE = " -DWITH_MAN=OFF -DPYTHON_INSTALL_DIR=${PYTHON_SITEPACKAGES_DIR} -DPYTHON_DESIRED=3"

BBCLASSEXTEND = "nativesdk"

FILES_${PN} = "${PYTHON_SITEPACKAGES_DIR}/ ${datadir}/ ${bindir}/ ${sysconfdir}/dnfdragora "
