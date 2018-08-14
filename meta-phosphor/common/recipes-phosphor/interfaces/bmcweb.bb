inherit obmc-phosphor-systemd
inherit useradd

USERADD_PACKAGES = "${PN}"

# add a user called httpd for the server to assume
USERADD_PARAM_${PN} = "-r -s /usr/sbin/nologin bmcweb"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=a6a4edad4aed50f39a66d098d74b265b"

SRC_URI = "git://github.com/openbmc/bmcweb.git"

PV = "1.0+git${SRCPV}"
SRCREV = "b9845d9e0155564a7b1f4a52df2c0d99dbd0d4ec"

S = "${WORKDIR}/git"

DEPENDS = "openssl zlib boost libpam sdbusplus gtest nlohmann-json libtinyxml2 "

FILES_${PN} += "${datadir}/** "

inherit cmake

EXTRA_OECMAKE = "-DBMCWEB_BUILD_UT=OFF -DYOCTO_DEPENDENCIES=ON"

SYSTEMD_SERVICE_${PN} += "bmcweb.service bmcweb.socket"

FULL_OPTIMIZATION = "-Os -pipe "

do_install_append() {
    rm -rf ${D}${includedir}/dbus
    rm -rf ${D}${libdir}/cmake
}
