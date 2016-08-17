SUMMARY = "GNU All Mobile Managment Utilities"
SECTION = "console/network"
DEPENDS = "cmake-native virtual/libiconv libdbi mysql5 glib-2.0 udev libgudev"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a17cb0a873d252440acfdf9b3d0e7fbf"
HOMEPAGE = "http://www.gammu.org/"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}/${PV}/${BP}.tar.bz2 \
    file://gammurc \
    file://gammu-smsdrc \
"

SRC_URI[md5sum] = "8ea16c6b3cc48097a8e62311fe0e25b9"
SRC_URI[sha256sum] = "de67caa102aa4c8fbed5300e5a0262e40411c4cc79f4379a8d34eed797968fc3"

inherit distutils cmake gettext

do_install_append() {
    # these files seem to only be used by symbian and trigger QA warnings
    rm -rf ${D}/usr/share/gammu
    #install default configuration files
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/gammurc ${D}${sysconfdir}/gammurc
    install -m 0644 ${WORKDIR}/gammu-smsdrc ${D}${sysconfdir}/gammu-smsdrc
}

EXTRA_OECONF = " \
    --enable-shared \
    --enable-backup \
    --enable-protection \
"

EXTRA_OECMAKE = " \
    -DWITH_CURL=OFF \
    -DWITH_BLUETOOTH=OFF \
    -DWITH_NOKIA_SUPPORT=OFF \
    -DWITH_IRDA=OFF \
    -DWITH_PYTHON=OFF \
    -DWITH_MySQL=ON \
    -DWITH_Postgres=OFF \
"

PACKAGES =+ "${PN}-smsd libgammu libgsmsd python-${PN}"

FILES_${PN} = "${bindir}/gammu ${bindir}/jadmaker ${sysconfdir}/bash_completion.d/gammu \
    ${bindir}/gammu-detect ${sysconfdir}/gammurc"
CONFFILES_${PN} = "${sysconfdir}/gammurc"
FILES_${PN}-smsd = "${bindir}/gammu-smsd* ${sysconfdir}/gammu-smsdrc"
CONFFILES_${PN}-smsd = "${sysconfdir}/gammu-smsdrc"
FILES_${PN}-dev += "${bindir}/gammu-config ${libdir}/*.so"
FILES_${PN}-dbg += "${bindir}/.debug ${libdir}/.debug ${PYTHON_SITEPACKAGES_DIR}/gammu/.debug"
FILES_libgammu = "${libdir}/libGammu.so.*"
FILES_libgsmsd = "${libdir}/libgsmsd.so.*"
FILES_python-${PN} = "${PYTHON_SITEPACKAGES_DIR}/gammu/*.??"

RDEPENDS_${PN} += "bash"
RDEPENDS_${PN}-dev += "bash"

# Fails to build with thumb-1 (qemuarm)
# gammu-1.32.0/libgammu/service/sms/gsmems.c:542:1: internal compiler error: in patch_jump_insn, at cfgrtl.c:1275
# |  }
# |  ^
# | Please submit a full bug report,
# | with preprocessed source if appropriate.
# | See <http://gcc.gnu.org/bugs.html> for instructions.
# | make[2]: *** [libgammu/CMakeFiles/libGammu.dir/service/sms/gsmems.o] Error 1
# | make[2]: *** Waiting for unfinished jobs....
ARM_INSTRUCTION_SET = "arm"
