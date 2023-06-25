SUMMARY = "Tools for working with sysfs"
DESCRIPTION = "Tools for working with the sysfs virtual filesystem.  The tool 'systool' can query devices by bus, class and topology."
HOMEPAGE = "http://linux-diag.sourceforge.net/Sysfsutils.html"

LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LICENSE:${PN} = "GPL-2.0-only"
LICENSE:libsysfs = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=dcc19fa9307a50017fca61423a7d9754 \
                    file://cmd/GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://lib/LGPL;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/linux-ras/sysfsutils.git;protocol=https;branch=master"

SRCREV = "da2f1f8500c0af6663a56ce2bff07f67e60a92e0"

S = "${WORKDIR}/git"

inherit autotools

PACKAGES =+ "libsysfs"
FILES:libsysfs = "${libdir}/lib*${SOLIBS}"
