SUMMARY = "A simple tool to wait for a specific signal over DBus"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/dbus-wait"
DESCRIPTION = "${SUMMARY}"
SECTION = "base"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "dbus"

SRCREV = "64bc7c8fae61ded0c4e555aa775911f84c56e438"
PV = "0.1+git"

SRC_URI = "git://git.yoctoproject.org/${BPN};branch=master;protocol=https"
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
