SUMARRY = "NIST Certified SCAP 1.2 toolkit"

DEPENDS:append = " xmlsec1"

require openscap.inc

inherit systemd

SRCREV = "55efbfda0f617e05862ab6ed4862e10dbee52b03"
SRC_URI = "git://github.com/OpenSCAP/openscap.git;branch=maint-1.3;protocol=https"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "oscap-remediate.service"

do_install:append () {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -D -m 0644 ${B}/oscap-remediate.service ${D}${systemd_system_unitdir}/oscap-remediate.service
    fi
}
