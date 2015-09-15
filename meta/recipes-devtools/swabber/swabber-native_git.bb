SUMMARY = "Tool to monitor and report on host system file usage"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/swabber"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

SRCREV = "2d1fe36fb0a4fdaae8823a9818a6785182d75e66"
PV = "0.0+git${SRCPV}"

S = "${WORKDIR}/git"

SRC_URI = "git://git.yoctoproject.org/swabber"

inherit native

do_configure () {
	:
}

do_install() {
  oe_runmake 'DESTDIR=${D}' install
}
