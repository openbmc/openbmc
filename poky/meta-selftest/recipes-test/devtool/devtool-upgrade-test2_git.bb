SUMMARY = "A simple tool to wait for a specific signal over DBus"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/dbus-wait"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "dbus"

# Note: this is intentionally not the latest version in the original .bb
SRCREV = "1a3e1343761b30750bed70e0fd688f6d3c7b3717"
PV = "0.1+git"
PR = "r2"

SRC_URI = "git://git.yoctoproject.org/dbus-wait;branch=master"
UPSTREAM_CHECK_COMMITS = "1"
RECIPE_NO_UPDATE_REASON = "This recipe is used to test devtool upgrade feature"

S = "${WORKDIR}/git"

EXCLUDE_FROM_WORLD = "1"

inherit autotools pkgconfig
