DESCRIPTION = "Blueman is a GTK+ Bluetooth Manager"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "bluez5 python3-pygobject python3-cython-native python3-setuptools-native intltool-native"

inherit autotools systemd gsettings python3native gtk-icon-cache

SRC_URI = " \
    git://github.com/blueman-project/blueman.git \
    file://0001-Search-for-cython3.patch \
"
SRCREV = "c4a03417e81e21543d4568e8e7f7de307582eb50"
PV = "2.0.5+git${SRCPV}"
S = "${WORKDIR}/git"

EXTRA_OECONF = " \
    --disable-runtime-deps-check \
    --disable-schemas-compile \
"

SYSTEMD_SERVICE_${PN} = "${BPN}-mechanism.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

RRECOMENDS_${PN} += "adwaita-icon-theme"
RDEPENDS_${PN} += " \
    python3-dbus \
    packagegroup-tools-bluetooth \
"

PACKAGECONFIG[thunar] = "--enable-thunar-sendto,--disable-thunar-sendto,,thunar"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/Thunar \
    ${systemd_user_unitdir} \
    ${exec_prefix}${systemd_system_unitdir} \
    ${PYTHON_SITEPACKAGES_DIR} \
"

FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/_blueman.a"
