SUMMARY = "A daemon that handles the userspace side of the LIO TCM-User backstore"
LICENSE = "Apache-2.0 OR LGPLv2.1"
LIC_FILES_CHKSUM = " \
	file://LICENSE.LGPLv2.1;md5=a7ef827a98fa240d37910ac4e8ce3f53 \
	file://LICENSE.Apache2;md5=6c4db32a2fa8717faffa1d4f10136f47 \
	"

SRCREV = "7b9e5006a30df77ea3f24594b69d4279b2a405dd"

SRC_URI = "git://github.com/open-iscsi/tcmu-runner.git;protocol=https;branch=main;tag=v${PV} \
           file://0001-CMakeLists-generate-gdbus-code-with-a-relative-path.patch \
           file://0002-qcow-fix-cache-count-reset-loops.patch \
           file://0003-target-alua-initialize-list_for_each-iterator-variables.patch \
           "

inherit cmake pkgconfig systemd

DEPENDS = "gtk4 libnl gperftools glib-2.0-native"

EXTRA_OECMAKE = "-DCMAKE_POLICY_VERSION_MINIMUM=3.5 \
		 -Dwith-rbd=false \
		 -Dwith-glfs=false \
                "

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

PACKAGECONFIG[systemd] = "-DSUPPORT_SYSTEMD=ON,,"

SYSTEMD_SERVICE:${PN} = "tcmu-runner.service"
SYSTEMD_AUTO_ENABLE = "enable"

FILES:${PN} += " \
    ${systemd_user_unitdir}/lib \
    ${systemd_user_unitdir}/tcmu-runner \
    ${systemd_system_unitdir}/tcmu-runner.service \
    ${datadir}/dbus-1 \
"
