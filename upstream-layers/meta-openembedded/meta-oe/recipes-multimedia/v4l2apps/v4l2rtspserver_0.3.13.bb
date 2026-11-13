SUMMARY = "V4L2rtsp streaming server"
LICENSE = "Unlicense"
LIC_FILES_CHKSUM = "file://Unlicense.txt;md5=d88e9e08385d2a17052dac348bde4bc1"

SRC_URI = "gitsm://github.com/mpromonet/v4l2rtspserver.git;branch=master;protocol=https;tag=v${PV}"
SRCREV = "fafc8cbd1c93bf0f7453efa8b7718c68941fd5eb"

inherit pkgconfig cmake systemd

DEPENDS += "live555 openssl"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'alsa systemd', d)} log4cpp"
PACKAGECONFIG[alsa] = "-DALSA=ON,-DALSA=OFF,alsa-lib"
PACKAGECONFIG[log4cpp] = "-DLOG4CPP=ON,-DLOG4CPP=OFF,log4cpp"
PACKAGECONFIG[systemd] = "-DSYSTEMD=ON,-DSYSTEMD=OFF,systemd"

# Keep CMake from running "git describe", Hand it the version instead.
EXTRA_OECMAKE += "-DCMAKE_DISABLE_FIND_PACKAGE_Git=ON"
export V4L2RTSPSERVER_VERSION = "${PV}"

SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('PACKAGECONFIG', 'systemd', 'v4l2rtspserver.service', '', d)}"
# The unit streams /dev/video0 to the network; let the image opt in.
SYSTEMD_AUTO_ENABLE = "disable"
