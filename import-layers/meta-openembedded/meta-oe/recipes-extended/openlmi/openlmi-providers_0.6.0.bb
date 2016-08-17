SUMMARY = "Set of basic CIM providers"
DESCRIPTION = "\
openlmi-providers is set of (usually) small CMPI providers (agents) for \
basic monitoring and management of host system using Common Information \
Model (CIM)."
HOMEPAGE = "http://www.openlmi.org/"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=7c13b3376cea0ce68d2d2da0a1b3a72c"
SECTION = "System/Management"
DEPENDS = "konkretcmpi-native konkretcmpi sblim-sfcb sblim-cmpi-devel cim-schema-exper lmsensors libuser swig swig-native dbus udev systemd-systemctl-native pciutils"

SRC_URI = "http://fedorahosted.org/released/${BPN}/${BP}.tar.gz \
           file://0001-fix-error.patch \
           file://0001-fix-lib64-can-not-be-shiped-in-64bit-target.patch \
          "
SRC_URI[md5sum] = "5904f23cf494946237cfbbdbe644a3cd"
SRC_URI[sha256sum] = "e2b2fbeaec45a83905d0da3b87da83904d9cd94c1b86312f844587b3fff11f56"

inherit cmake
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', ' -fuse-ld=bfd ', '', d)}"
EXTRA_OECMAKE = " \
                 -DWITH-DEVASSISTANT=OFF \
                 -DWITH-JOURNALD=OFF \
                 -DWITH-SERVICE=OFF \
                 -DWITH-SERVICE-LEGACY=ON \
                 -DWITH-ACCOUNT=OFF \
                 -DWITH-PCP=OFF \
                 -DWITH-REALMD=OFF \
                 -DWITH-FAN=OFF \
                 -DWITH-LOCALE=OFF \
                 -DWITH-INDSENDER=OFF \
                 -DWITH-JOBMANAGER=OFF \
                 -DWITH-SSSD=OFF \
                 -DWITH-SELINUX=OFF \
                 -DWITH-SOFTWARE-DBUS=ON \
                 ${@base_conditional("libdir", "/usr/lib64", "-DLIB_SUFFIX=64", "", d)} \
                 ${@base_conditional("libdir", "/usr/lib32", "-DLIB_SUFFIX=32", "", d)} \
               "

do_configure_prepend() {
    export CMAKE_INSTALL_DATDIR="${STAGING_DATADIR}"
}

do_install_append() {
    if [ -d ${D}${prefix}${sysconfidr} ]; then
        mv ${D}${prefix}${sysconfdir} ${D}${sysconfdir}
    fi
}

FILES_${PN} =+ "${libdir}/cmpi/libcmpiLMI* ${prefix}/libexec*"
FILES_${PN}-dev =+ "${datadir}/cmake*"
FILES_${PN}-dbg =+ "${libdir}/cmpi/.debug*"

RDEPENDS_${PN} = "python"
