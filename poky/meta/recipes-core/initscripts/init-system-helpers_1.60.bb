SUMMARY = "helper tools for all init systems"
DESCRIPTION = "This package contains helper tools that are necessary for switching between \
the various init systems that Debian contains (e. g. sysvinit or \
systemd). An example is deb-systemd-helper, a script that enables systemd unit \
files without depending on a running systemd. \
\
It also includes the \"service\", \"invoke-rc.d\", and \"update-rc.d\" scripts which \
provide an abstraction for enabling, disabling, starting, and stopping \
services for all supported Debian init systems as specified by the policy. \
\
While this package is maintained by pkg-systemd-maintainers, it is NOT \
specific to systemd at all. Maintainers of other init systems are welcome to \
include their helpers in this package."
HOMEPAGE = "https://salsa.debian.org/debian/init-system-helpers"
SECTION = "base"
LICENSE = "BSD-3-Clause & GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=ee2b1830fcfead84d07bc060ec43e072"

SRCREV = "dbd9197569c0935029acd5c9b02b84c68fd937ee"
SRC_URI = "git://salsa.debian.org/debian/init-system-helpers.git;protocol=https"

S = "${WORKDIR}/git"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
	install -d -m 0755 ${D}${sbindir}
	install -m 0755 ${S}/script/invoke-rc.d ${D}${sbindir}
	install -m 0755 ${S}/script/service ${D}${sbindir}
}

PACKAGES += "${PN}-invoke-rc.d ${PN}-service"

FILES_${PN} = ""
FILES_${PN}-invoke-rc.d = "${sbindir}/invoke-rc.d"
FILES_${PN}-service = "${sbindir}/service"

ALLOW_EMPTY_${PN} = "1"

RRECOMMENDS_${PN} += "${PN}-invoke-rc.d ${PN}-service"
