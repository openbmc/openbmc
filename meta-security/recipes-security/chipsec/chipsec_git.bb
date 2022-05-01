SUMMARY = "CHIPSEC: Platform Security Assessment Framework"

DESCRIPTION = "CHIPSEC is a framework for analyzing the security \
               of PC platforms including hardware, system firmware \
               (BIOS/UEFI), and platform components."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=bc2d1f9b427be5fb63f6af9da56f7c5d"

SRC_URI = "git://github.com/chipsec/chipsec.git;branch=master;protocol=https \
          "

SRCREV = "b2a61684826dc8b9f622a844a40efea579cd7e7d"

COMPATIBLE_HOST = "(i.86|x86_64).*-linux"

S = "${WORKDIR}/git"
EXTRA_OEMAKE = "CC='${CC}' LDFLAGS='${LDFLAGS}' CFLAGS='${CFLAGS}'"

DEPENDS = "virtual/kernel nasm-native python3-setuptools-native"
RDEPENDS:${PN} += "python3 python3-modules"

inherit module setuptools3

do_compile:append() {
	cd ${S}/drivers/linux
	oe_runmake  KSRC=${STAGING_KERNEL_BUILDDIR}
}

do_install:append() {
	install -m 0644 ${S}/drivers/linux/chipsec.ko ${D}${PYTHON_SITEPACKAGES_DIR}/chipsec/helper/linux
}

FILES:${PN} += "${exec_prefix} \
"
