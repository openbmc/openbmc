DESCRIPTION = "Linux software for Dediprog SF100 and SF600 SPI flash programmers"
SECTION = "devel"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a23a74b3f4caf9616230789d94217acb"

DEPENDS = "libusb"

SRCREV = "42edbcc60217f3fb39d5c6e5a68bedaa32844482"
SRC_URI = " \
    git://github.com/DediProgSW/SF100Linux.git;protocol=https;branch=master;tag=V${PV},x \
    "

EXTRA_OEMAKE = "NOSTRIP=1 DESTDIR=${D} PREFIX=${prefix}"

do_install() {
    oe_runmake install
}

do_install:append:class-nativesdk() {
    # QA override: omit packaging dediprog's udev rule under /etc/udev.
    # The file resides outside the nativesdk ${prefix} and must not pollute the
    # host environment.
    rm -rf ${D}/etc
}

FILES:${PN} += " \
    ${bindir} \
    ${datadir}/DediProg \
"

inherit pkgconfig

BBCLASSEXTEND += " native nativesdk"
