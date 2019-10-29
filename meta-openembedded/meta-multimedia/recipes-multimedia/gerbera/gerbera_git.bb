Description = "Gerbera - An UPnP media server"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=25cdec9afe3f1f26212ead6bd2f7fac8"

SRC_URI = "git://github.com/v00d00/gerbera.git;protocol=https \
"

PV = "1.3.2"
SRCREV = "42b035ea9098c02af503d6391a0ed56d973aaf23"

S = "${WORKDIR}/git"

DEPENDS = "expat zlib curl libupnp e2fsprogs sqlite3 libnsl2"

SYSTEMD_SERVICE_${PN} = "gerbera.service"

inherit cmake systemd

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=TRUE,-DWITH_SYSTEMD=FALSE,systemd"
PACKAGECONFIG[taglib] = "-DWITH_TAGLIB=TRUE,-DWITH_TAGLIB=FALSE,taglib"
EXTRA_OECMAKE = "-DWITH_JS=FALSE -DWITH_MAGIC=FALSE -DWITH_EXIF=FALSE -DLIBUUID_INCLUDE_DIRS=${STAGING_INCDIR} -DLIBUUID_LIBRARIES=-luuid"

do_install_append() {
    install -d ${D}/root/.config/
}

FILES_${PN} += "/root/.config/"
