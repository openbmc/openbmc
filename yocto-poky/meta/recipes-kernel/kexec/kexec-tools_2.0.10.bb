require kexec-tools.inc
export LDFLAGS = "-L${STAGING_LIBDIR}"
EXTRA_OECONF = " --with-zlib=yes"

SRC_URI += "file://kexec-tools-Refine-kdump-device_tree-sort.patch \
            file://kexec-aarch64.patch \
            file://kexec-x32.patch \
            file://0002-powerpc-change-the-memory-size-limit.patch \
            file://0001-purgatory-Pass-r-directly-to-linker.patch \
         "

SRC_URI[md5sum] = "6cb4d22bcec71b6e070aa8e9d990a5e6"
SRC_URI[sha256sum] = "c31bb83deef9547a28e8cfc1f0916e70f8e6b92a6bd2ef7077e12e3338239af3"

PACKAGES =+ "kexec kdump vmcore-dmesg"

ALLOW_EMPTY_${PN} = "1"
RRECOMMENDS_${PN} = "kexec kdump vmcore-dmesg"

FILES_kexec = "${sbindir}/kexec"
FILES_kdump = "${sbindir}/kdump"
FILES_vmcore-dmesg = "${sbindir}/vmcore-dmesg"
