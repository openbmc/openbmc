# Copyright (C) 2012 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

require kmod.inc

DEPENDS += "zlib"
PROVIDES += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"
RPROVIDES_${PN} += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"
RCONFLICTS_${PN} += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"
RREPLACES_${PN} += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"

# to force user to remove old module-init-tools and replace them with kmod variants
RCONFLICTS_libkmod2 += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"

# autotools set prefix to /usr, however we want them in /bin and /sbin
bindir = "${base_bindir}"
sbindir = "${base_sbindir}"
# libdir = "${base_libdir}"

do_install_append () {
        install -dm755 ${D}${base_bindir}
        install -dm755 ${D}${base_sbindir}
        # add symlinks to kmod
        lnr ${D}${base_bindir}/kmod ${D}${base_bindir}/lsmod
        for tool in insmod rmmod depmod modinfo modprobe; do
                lnr ${D}${base_bindir}/kmod ${D}${base_sbindir}/${tool}
        done
        # configuration directories
        install -dm755 ${D}${base_libdir}/depmod.d
        install -dm755 ${D}${base_libdir}/modprobe.d
        install -dm755 ${D}${sysconfdir}/depmod.d
        install -dm755 ${D}${sysconfdir}/modprobe.d

        # install depmod.d file for search/ dir
        install -Dm644 "${WORKDIR}/depmod-search.conf" "${D}${base_libdir}/depmod.d/search.conf"
}

do_compile_prepend() {
            sed -i 's/ac_pwd=/#ac_pwd=/' config.status ; sed -i "/#ac_pwd=/a\ac_pwd='.'" config.status
}

inherit update-alternatives bash-completion

ALTERNATIVE_PRIORITY = "60"

ALTERNATIVE_kmod = "insmod modprobe rmmod modinfo bin-lsmod lsmod depmod"

ALTERNATIVE_LINK_NAME[insmod] = "${base_sbindir}/insmod"
ALTERNATIVE_LINK_NAME[modprobe] = "${base_sbindir}/modprobe"
ALTERNATIVE_LINK_NAME[rmmod] = "${base_sbindir}/rmmod"
ALTERNATIVE_LINK_NAME[modinfo] = "${base_sbindir}/modinfo"
ALTERNATIVE_LINK_NAME[bin-lsmod] = "${base_bindir}/lsmod"

ALTERNATIVE_LINK_NAME[lsmod] = "${base_sbindir}/lsmod"
ALTERNATIVE_TARGET[lsmod] = "${base_bindir}/lsmod.${BPN}"

ALTERNATIVE_LINK_NAME[depmod] = "${base_sbindir}/depmod"

PACKAGES =+ "libkmod"

FILES_libkmod = "${base_libdir}/libkmod*${SOLIBS} ${libdir}/libkmod*${SOLIBS}"
FILES_${PN} += "${base_libdir}/depmod.d ${base_libdir}/modprobe.d"
