require kexec-tools.inc
export LDFLAGS = "-L${STAGING_LIBDIR}"
EXTRA_OECONF = " --with-zlib=yes"

SRC_URI += "file://kexec-tools-Refine-kdump-device_tree-sort.patch \
            file://kexec-aarch64.patch \
            file://kexec-x32.patch \
            file://0002-powerpc-change-the-memory-size-limit.patch \
            file://0001-purgatory-Pass-r-directly-to-linker.patch \
            file://0001-vmcore-dmesg-Define-_GNU_SOURCE.patch \
         "

SRC_URI[md5sum] = "86de066859f289048f1b286af6f03f78"
SRC_URI[sha256sum] = "84f652ebf1de3f7b9de757a50cdbf6d5639d88c1d5b5ef9f525edde5ef9590c2"

PACKAGES =+ "kexec kdump vmcore-dmesg"

ALLOW_EMPTY_${PN} = "1"
RRECOMMENDS_${PN} = "kexec kdump vmcore-dmesg"

FILES_kexec = "${sbindir}/kexec"
FILES_kdump = "${sbindir}/kdump ${sysconfdir}/init.d/kdump \
               ${sysconfdir}/sysconfig/kdump.conf"
FILES_vmcore-dmesg = "${sbindir}/vmcore-dmesg"

inherit update-rc.d

INITSCRIPT_PACKAGES = "kdump"
INITSCRIPT_NAME_kdump = "kdump"
INITSCRIPT_PARAMS_kdump = "start 56 2 3 4 5 . stop 56 0 1 6 ."

do_install_append () {
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/kdump ${D}${sysconfdir}/init.d/kdump
        install -d ${D}${sysconfdir}/sysconfig
        install -m 0644 ${WORKDIR}/kdump.conf ${D}${sysconfdir}/sysconfig
}
