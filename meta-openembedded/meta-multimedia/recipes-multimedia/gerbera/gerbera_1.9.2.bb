Description = "Gerbera - An UPnP media server"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=25cdec9afe3f1f26212ead6bd2f7fac8"

SRC_URI = "git://github.com/v00d00/gerbera.git;protocol=https;branch=master \
           file://0001-Fix-for-fmt-8.0.patch \
          "

SRCREV = "3b93d362ec33f738868a4e896a2c9ee8e9d7a92a"

S = "${WORKDIR}/git"

DEPENDS = "expat fmt spdlog pugixml libebml libmatroska zlib curl libupnp e2fsprogs sqlite3 libnsl2"

SYSTEMD_SERVICE:${PN} = "gerbera.service"

inherit cmake pkgconfig systemd

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-DWITH_SYSTEMD=TRUE,-DWITH_SYSTEMD=FALSE,systemd"
PACKAGECONFIG[taglib] = "-DWITH_TAGLIB=TRUE,-DWITH_TAGLIB=FALSE,taglib"
EXTRA_OECMAKE = "-DWITH_JS=FALSE -DWITH_MAGIC=FALSE -DWITH_EXIF=FALSE -DLIBUUID_INCLUDE_DIRS=${STAGING_INCDIR} -DLIBUUID_LIBRARIES=-luuid"

do_install:append() {
    install -d ${D}/root/.config/
}

FILES:${PN} += "/root/.config/"

SECURITY_CFLAGS:riscv64 = "${SECURITY_NOPIE_CFLAGS}"
