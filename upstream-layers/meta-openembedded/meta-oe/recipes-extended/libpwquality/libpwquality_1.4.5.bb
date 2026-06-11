DESCRIPTION = "Library for password quality checking and generating random passwords"
HOMEPAGE = "https://github.com/libpwquality/libpwquality"
SECTION = "devel/lib"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bd2f1386df813a459a0c34fde676fc2"

DEPENDS = "cracklib"

SRC_URI = "git://github.com/libpwquality/libpwquality;branch=master;protocol=https \
    file://0001-Use-setuptools-instead-of-distutils.patch \
    file://0002-Makefile.am-respect-PYTHONSITEDIR.patch \
"
SRCREV = "5490e96a3dd6ed7371435ca5b3ccef98bdb48b5a"


inherit autotools-brokensep gettext
inherit_defer ${@bb.utils.contains('PACKAGECONFIG', 'python3', 'setuptools3-base', '', d)}

do_configure:prepend() {
    cp ${STAGING_DATADIR_NATIVE}/gettext/ABOUT-NLS ${AUTOTOOLS_AUXDIR}/
}

export PYTHON_DIR
export BUILD_SYS
export HOST_SYS

EXTRA_OECONF += "--libdir=${libdir} \
                 --with-securedir=${base_libdir}/security \
"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} python3"
PACKAGECONFIG[pam] = "--enable-pam,--disable-pam,libpam"
PACKAGECONFIG[python3] = "--enable-python-bindings --with-python-rev=${PYTHON_BASEVERSION} --with-python-binary=${STAGING_BINDIR_NATIVE}/python3-native/python3 --with-pythonsitedir=${PYTHON_SITEPACKAGES_DIR},--disable-python-bindings,python3-setuptools-native"

FILES:${PN} += "${base_libdir}/security/pam_pwquality.so"
FILES:${PN}-dbg += "${base_libdir}/security/.debug"
FILES:${PN}-staticdev += "${base_libdir}/security/pam_pwquality.a"
FILES:${PN}-dev += "${base_libdir}/security/pam_pwquality.la"
