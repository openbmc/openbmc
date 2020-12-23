DESCRIPTION = "Library for password quality checking and generating random passwords"
HOMEPAGE = "https://github.com/libpwquality/libpwquality"
SECTION = "devel/lib"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bd2f1386df813a459a0c34fde676fc2"

SRCNAME = "libpwquality"
SRC_URI = "https://github.com/${SRCNAME}/${SRCNAME}/releases/download/${SRCNAME}-${PV}/${SRCNAME}-${PV}.tar.bz2 \
           file://add-missing-python-include-dir-for-cross.patch \
"

SRC_URI[md5sum] = "1fe43f6641dbf1e1766e2a02cf68a9c3"
SRC_URI[sha256sum] = "d43baf23dc6887fe8f8e9b75cabaabc5f4bbbaa0f9eff44278d276141752a545"

UPSTREAM_CHECK_URI = "https://github.com/libpwquality/libpwquality/releases"

S = "${WORKDIR}/${SRCNAME}-${PV}"

DEPENDS = "cracklib virtual/gettext"

inherit autotools distutils3-base gettext

B = "${S}"

export PYTHON_DIR
export BUILD_SYS
export HOST_SYS

EXTRA_OECONF += "--with-python-rev=${PYTHON_BASEVERSION} \
                 --with-python-binary=${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
                 --with-pythonsitedir=${PYTHON_SITEPACKAGES_DIR} \
                 --libdir=${libdir} \
"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
PACKAGECONFIG[pam] = "--enable-pam, --disable-pam, libpam"

FILES_${PN} += "${libdir}/security/pam_pwquality.so"
FILES_${PN}-dbg += "${libdir}/security/.debug"
FILES_${PN}-staticdev += "${libdir}/security/pam_pwquality.a"
FILES_${PN}-dev += "${libdir}/security/pam_pwquality.la"
