DESCRIPTION = "This is a small collection of power management \
               test and investigation tools"
HOMEPAGE = "http://lesswatts.org/projects/acpi"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

PV = "20130209+git${SRCPV}"

SRC_URI = "git://github.com/anyc/pmtools.git \
    file://pmtools-switch-to-dynamic-buffer-for-huge-ACPI-table.patch \
"
SRCREV = "3ebe0e54c54061b4c627236cbe35d820de2e1168"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"

S = "${WORKDIR}/git"

do_configure[noexec] = "1"
do_compile() {
	oe_runmake
}

do_install() {
	install -d ${D}${bindir} ${D}${docdir}
	install -m 755 ${S}/acpidump/acpidump ${D}${bindir}
	install -m 755 ${S}/acpixtract/acpixtract ${D}${bindir}
	install -m 755 ${S}/madt/madt ${D}${bindir}
	install -m 644 ${S}/README ${D}${docdir}
}
