SUMMARY = "The Smart Package Manager"
DESCRIPTION = "The Smart Package Manager project has the ambitious objective of creating \
smart and portable algorithms for solving adequately the problem of managing software \
upgrades and installation."

HOMEPAGE = "http://labix.org/smart/"
SECTION = "devel/python"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "python rpm gettext-native python-rpm"
SRCNAME = "smart"

SRC_URI = "\
          git://github.com/smartpm/smart.git \
          file://smart-recommends.patch \
          file://smart-channelsdir.patch \
          file://smart-rpm-transaction-failure-check.patch \
          file://smart-attempt.patch \
          file://smart-attempt-fix.patch \
          file://smart-add-for-rpm-ignoresize-check.patch \
          file://smart-already-installed-message.patch \
          file://smart-set-noprogress-for-pycurl.patch \
          file://smart-cache.py-getPackages-matches-name-version.patch \
          file://smart-channel-remove-all.patch \
          file://smart-locale.patch \
          file://smartpm-rpm5-support-check-signatures.patch \
          file://smart-add-deugging-when-targetpath-is-empty.patch \
          file://channels-rpm_sys-use-md5sum-instead-of-mtime-as-the-.patch \
         "

SRCREV = "407a7eca766431257dcd1da15175cc36a1bb22d0"
PV = "1.5+git${SRCPV}"

S = "${WORKDIR}/git"

# Options - rpm, qt4, gtk
PACKAGECONFIG ??= "rpm"

RPM_RDEP = "${PN}-backend-rpm"
QT_RDEP = "${PN}-interface-qt4"
GTK_RDEP = "${PN}-interface-gtk"

RPM_RDEP_class-native = ""
QT_RDEP_class-native = ""
GTK_RDEP_class-native = ""

RPM_RDEP_class-nativesdk = ""
QT_RDEP_class-nativesdk = ""
GTK_RDEP_class-nativesdk = ""

PACKAGECONFIG[rpm] = ",,rpm,${RPM_RDEP}"
PACKAGECONFIG[qt4] = ",,qt4-x11,${QT_RDEP}"
PACKAGECONFIG[gtk] = ",,gtk+,${GTK_RDEP}"

inherit distutils

do_install_append() {
   # We don't support the following items
   rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/smart/backends/slack
   rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/smart/backends/arch
   rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/smart/interfaces/qt

   # Temporary, debian support in OE is missing the python module
   rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/smart/plugins/aptchannelsync.py*
   rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/smart/plugins/debdir.py*
   rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/smart/backends/deb

   # Disable automatic channel detection
   rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/smart/plugins/detectsys.py*

   # Disable landscape support
   rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/smart/plugins/landscape.py*

   # Disable urpmi channel support
   rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/smart/plugins/urpmichannelsync.py*

   # Disable yum channel support
   rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/smart/plugins/yumchannelsync.py*

   # Disable zypper channel support
   rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/smart/plugins/zyppchannelsync.py*

   if [ -z "${@bb.utils.contains('PACKAGECONFIG', 'rpm', 'rpm', '', d)}" ]; then
      rm -f ${D}${PYTHON_SITEPACKAGES_DIR}/smart/plugins/rpmdir.py*
      rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/smart/backends/rpm
   fi

   if [ -z "${@bb.utils.contains('PACKAGECONFIG', 'qt4', 'qt4', '', d)}" ]; then
      rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/smart/interfaces/qt4
   fi

   if [ -z "${@bb.utils.contains('PACKAGECONFIG', 'gtk+', 'gtk', '', d)}" ]; then
      rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/smart/interfaces/gtk
   fi
}

add_native_wrapper() {
        create_wrapper ${D}/${bindir}/smart \
		RPM_USRLIBRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('libdir', True), d.getVar('bindir', True))}/rpm \
		RPM_ETCRPM='$'{RPM_ETCRPM-'`dirname $''realpath`'/${@os.path.relpath(d.getVar('sysconfdir', True), d.getVar('bindir', True))}/rpm} \
		RPM_LOCALEDIRRPM='`dirname $''realpath`'/${@os.path.relpath(d.getVar('datadir', True), d.getVar('bindir', True))}/locale
}

do_install_append_class-native() {
        sed -i -e 's|^#!.*/usr/bin/env python|#! /usr/bin/env nativepython|' ${D}${bindir}/smart
        add_native_wrapper
}

do_install_append_class-nativesdk() {
        add_native_wrapper
}

PACKAGES = "${PN}-dev ${PN}-dbg ${PN}-doc smartpm \
            ${@bb.utils.contains('PACKAGECONFIG', 'rpm', '${PN}-backend-rpm', '', d)} \
            ${@bb.utils.contains('PACKAGECONFIG', 'qt4', '${PN}-interface-qt4', '', d)} \
            ${@bb.utils.contains('PACKAGECONFIG', 'gtk', '${PN}-interface-gtk', '', d)} \
            ${PN}-interface-images ${PN}"

RDEPENDS_smartpm = "${PN}"

RDEPENDS_${PN} += "${PN}-backend-rpm python-codecs python-textutils python-xml python-fcntl \
                   python-pickle python-crypt python-compression python-shell \
                   python-resource python-netclient python-threading python-unixadmin python-pprint"
RDEPENDS_${PN}_class-native = ""

RDEPENDS_${PN}-backend-rpm = "python-rpm"

RDEPENDS_${PN}-interface-qt4 = "qt4-x11 ${PN}-interface-images"
RDEPENDS_${PN}-interface-gtk = "gtk+ ${PN}-interface-images"

FILES_smartpm = "${bindir}/smart"

FILES_${PN}-backend-rpm = "${PYTHON_SITEPACKAGES_DIR}/smart/backends/rpm"

FILES_${PN}-interface-qt4 = "${PYTHON_SITEPACKAGES_DIR}/smart/interfaces/qt4"
FILES_${PN}-interface-gtk = "${PYTHON_SITEPACKAGES_DIR}/smart/interfaces/gtk"
FILES_${PN}-interface-images = "${datadir}/${baselib}/python*/site-packages/smart/interfaces/images"

BBCLASSEXTEND = "native nativesdk"

