require kexec-tools.inc
export LDFLAGS = "-L${STAGING_LIBDIR}"
EXTRA_OECONF = " --with-zlib=yes"

SRC_URI += " \
            file://kexec-aarch64.patch \
            file://kexec-x32.patch \
            file://0002-powerpc-change-the-memory-size-limit.patch \
            file://0001-purgatory-Pass-r-directly-to-linker.patch \
            file://0001-vmcore-dmesg-Define-_GNU_SOURCE.patch \
         "

SRC_URI[md5sum] = "10ddaae0e86af54407b164a1f5a39cc3"
SRC_URI[sha256sum] = "cc7b60dad0da202004048a6179d8a53606943062dd627a2edba45a8ea3a85135"

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
