#
# Copyright 2022 Armin Kuster <akuster808@gmail.com>
#
SUMMARY = "Linux namespaces and seccomp-bpf sandbox"
DESCRIPTION = "Firejail is a SUID sandbox program that reduces the risk of security breaches \
by restricting the running environment of untrusted applications using Linux namespaces, \
seccomp-bpf and Linux capabilities."

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
LICENSE = "GPL-2.0-only"

SRCREV = "2551bc71f14052344666f3ca2ad67f5b798020b9"
SRC_URI = "git://github.com/netblue30/firejail.git;protocol=https;branch=master \
           file://exclude_seccomp_util_compiles.patch \
           "

DEPENDS = "libseccomp"

S = "${UNPACKDIR}/git"

inherit autotools-brokensep pkgconfig bash-completion features_check

REQUIRED_DISTRO_FEATURES = "seccomp"

PACKAGECONFIG ?= ""
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'apparmor', 'apparmor', '', d)}"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)}"
PACKAGECONFIG += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"

PACKAGECONFIG[apparmor] = "--enable-apparmor, --disable-apparmor, apparmor, apparmor"
PACKAGECONFIG[selinux] = "--enable-selinux, --disable-selinux, libselinux"
PACKAGECONFIG[x11] = " --enable-x11, --disable-x11, "
PACKAGECONFIG[dbusproxy] = ", --disable-dbusproxy, "
PACKAGECONFIG[notmpfs] = ", --disable-usertmpfs  ,"
PACKAGECONFIG[nofiretunnel] = ", --disable-firetunnel , "
PACKAGECONFIG[noprivatehome] = ", --disable-private-home, "
PACKAGECONFIG[nochroot] = ", --disable-chroot, "
PACKAGECONFIG[nonetwork] = ", --disable-network, "
PACKAGECONFIG[nouserns] = ", --disable-userns, "
PACKAGECONFIG[nofiletransfer] = ", --disable-file-transfer, "
PACKAGECONFIG[nosuid] = ", --disable-suid, "

EXTRA_OECONF = "--disable-man --enable-busybox-workaround"

PACKAGES:append = " ${PN}-vim ${PN}-zsh"

FILES:${PN}-vim = "${datadir}/vim/"
FILES:${PN}-zsh = "${datadir}/zsh/"
FILES:${PN}-dev = "${datadir}/gtksourceview-5/"

pkg_postinst_ontarget:${PN} () {
    ${libdir}/${BPN}/fseccomp default ${libdir}/${BPN}/seccomp
    ${libdir}/${BPN}/fsec-optimize ${libdir}/${BPN}/seccomp
    ${libdir}/${BPN}/fseccomp default ${libdir}/${BPN}/seccomp.debug allow-debuggers
    ${libdir}/${BPN}/fsec-optimize ${libdir}/${BPN}/seccomp.debug
    ${libdir}/${BPN}/fseccomp secondary 32 ${libdir}/${BPN}/seccomp.32
    ${libdir}/${BPN}/fsec-optimize ${libdir}/${BPN}/seccomp.32
    ${libdir}/${BPN}/fseccomp secondary block ${libdir}/${BPN}/seccomp.block_secondary
    ${libdir}/${BPN}/fseccomp memory-deny-write-execute ${libdir}/${BPN}/seccomp.mdwx
}

COMPATIBLE_MACHINE:x86_64 = "x86_64"
COMPATIBLE_MACHINE:arm64 = "arch64"

RDEPENDS:${PN} = "bash"
