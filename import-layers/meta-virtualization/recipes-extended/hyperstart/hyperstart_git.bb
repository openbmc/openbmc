SUMMARY = "The tiny Init service for HyperContainer"
DESCRIPTION = "The init Task for HyperContainer"

LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fa818a259cbed7ce8bc2a22d35a464fc"

inherit autotools-brokensep 

SRC_URI = "git://github.com/hyperhq/hyperstart.git"

SRCREV = "ad48a3230836f59ada163659cde151a37522068b"
PV = "v0.2+git${SRCREV}"

S = "${WORKDIR}/git"

CACHED_CONFIGUREVARS = "ac_cv_file__usr_include_linux_vm_sockets_h=true"

do_install() {
	install -d ${D}/var/lib/hyper/

        install -m644 ${S}/build/hyper-initrd.img ${D}/var/lib/hyper/
        install -m644 ${S}/build/arch/x86_64/kernel ${D}/var/lib/hyper/
}

FILES_${PN} += "/var/lib/hyper"
