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
LICENSE = "BSD-3-Clause & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=c4ec20aa158fa9de26ee1accf78dcaae"

SRCREV = "7b1a5359d99c1667f7b81380c06686ddc57b8e71"
SRC_URI = "git://salsa.debian.org/debian/init-system-helpers.git;protocol=https;branch=master"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))(?!_exp)"

S = "${WORKDIR}/git"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
	install -d -m 0755 ${D}${sbindir}
	install -m 0755 ${S}/script/invoke-rc.d ${D}${sbindir}
	install -m 0755 ${S}/script/service ${D}${sbindir}
}

PACKAGES += "${PN}-invoke-rc.d ${PN}-service"

FILES:${PN} = ""
FILES:${PN}-invoke-rc.d = "${sbindir}/invoke-rc.d"
FILES:${PN}-service = "${sbindir}/service"

ALLOW_EMPTY:${PN} = "1"

RRECOMMENDS:${PN} += "${PN}-invoke-rc.d ${PN}-service"
