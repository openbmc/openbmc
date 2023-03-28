SUMMARY = "Firmware for using the ASPEED ColdFire FSI master"
SECTION = "kernel"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRCREV = "bae32e353a3641b5164211f6bf06c5620f6e384d"
SRC_URI = "git://github.com/ozbenh/cf-fsi.git;branch=master;protocol=https"

PR = "r1"
PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit allarch

do_compile() {
    :
}

firmware_dir="${nonarch_base_libdir}/firmware/"

do_install() {
    install -d ${D}${firmware_dir}
    install -m 0644 ${S}/dist-bin/cf-fsi-fw.bin ${D}${firmware_dir}
}

FILES:${PN} = "${firmware_dir}"
