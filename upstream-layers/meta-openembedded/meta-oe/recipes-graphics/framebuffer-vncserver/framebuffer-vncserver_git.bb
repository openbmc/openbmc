SUMMARY = "VNC server for Linux framebuffer devices"
DESCRIPTION = "\
    The goal is to access remote embedded Linux systems without X. Implemented \
    features: remote display, touchscreen, keyboard, rotation. Not implemented: \
    file transfer, ... \
"
HOMEPAGE = "https://github.com/ponty/framebuffer-vncserver"
BUGTRACKER = "https://github.com/ponty/framebuffer-vncserver/issues"
SECTION = "graphics"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8264535c0c4e9c6c335635c4026a8022"

DEPENDS = "libvncserver"

SRC_URI = "\
    git://github.com/ponty/framebuffer-vncserver.git;protocol=https;branch=master \
    file://cmake.patch \
"
SRCREV = "1963e57bebfde420baeecbb2c6848a2382488413"

inherit cmake systemd

do_install:append() {
    install -m 644 -D -t ${D}${systemd_system_unitdir} ${S}/fbvnc.service
}

SYSTEMD_SERVICE:${PN} = "fbvnc.service"
