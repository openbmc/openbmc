SUMMARY = "DMI (Desktop Management Interface) table related utilities"
HOMEPAGE = "http://www.nongnu.org/dmidecode/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=393a5ca445f6965873eca0259a17f833"

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/dmidecode/${BP}.tar.bz2"

COMPATIBLE_HOST = "(i.86|x86_64|aarch64|arm|powerpc|powerpc64).*-linux"

do_install() {
	oe_runmake DESTDIR="${D}" install
}

do_unpack_extra() {
	sed -i -e '/^prefix/s:/usr/local:${exec_prefix}:' ${S}/Makefile
}
addtask unpack_extra after do_unpack before do_patch

SRC_URI[md5sum] = "a406f3cbb27736491698697beeddb781"
SRC_URI[sha256sum] = "913ff3055d563a62a420789b8ee33b038de9afa18ea61254760ddf8ab87a5088"
