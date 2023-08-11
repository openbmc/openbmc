SUMMARY = "Remove idle cursor image from screen."
DESCRIPTION = "This is a rewrite of the popular tool unclutter, but using the x11-xfixes extension."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b25d2c4cca175f44120d1b8e67cb358d"

SRC_URI = "git://github.com/Airblader/unclutter-xfixes.git;branch=master;protocol=https \
           file://0001-build-use-autotools.patch"
SRCREV = "160ae3760a51126eb225ce77d83e4706eccd4ed9"

inherit autotools pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS = "libev libx11 libxi libxfixes"

S = "${WORKDIR}/git"

do_install:append() {
    # LICENSE is installed to /usr/share/licenses but we don't want it in the package
    rm -rf ${D}${datadir}
}
