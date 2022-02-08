SUMMARY = "Remove idle cursor image from screen."
DESCRIPTION = "This is a rewrite of the popular tool unclutter, but using the x11-xfixes extension."
AUTHOR = "Ingo Bürk"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b25d2c4cca175f44120d1b8e67cb358d"

SRC_URI = "git://github.com/Airblader/unclutter-xfixes.git;branch=master;protocol=https \
           file://0001-build-use-autotools.patch"
SRCREV = "10fd337bb77e4e93c3380f630a0555372778a948"

inherit autotools pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "libev libx11 libxi libxfixes"

S = "${WORKDIR}/git"

do_install_append() {
    # LICENSE is installed to /usr/share/licenses but we don't want it in the package
    rm -rf ${D}${datadir}
}
