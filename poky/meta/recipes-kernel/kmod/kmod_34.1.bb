# Copyright (C) 2012 Khem Raj <raj.khem@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Tools for managing Linux kernel modules"
DESCRIPTION = "kmod is a set of tools to handle common tasks with Linux kernel modules like \
               insert, remove, list, check properties, resolve dependencies and aliases."
HOMEPAGE = "http://kernel.org/pub/linux/utils/kernel/kmod/"
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LICENSE:libkmod = "LGPL-2.1-or-later"
SECTION = "base"

LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://libkmod/COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
                    file://tools/COPYING;md5=751419260aa954499f7abaabaa882bbe \
                   "
inherit bash-completion gtk-doc manpages meson pkgconfig update-alternatives

SRC_URI = "https://www.kernel.org/pub/linux/utils/kernel/${BPN}/${BP}.tar.xz \
           file://depmod-search.conf \
           "
SRC_URI[sha256sum] = "125957c9125fc5db1bd6a2641a1c9a6a0b500882fb8ccf7fb6483fcae5309b17"

EXTRA_OEMESON += "\
    -Ddistconfdir=${nonarch_base_libdir} \
    --bindir=${base_bindir} \
    --sbindir=${base_sbindir} \
    -Dtools=true \
"

PACKAGECONFIG ??= "zlib xz openssl"
PACKAGECONFIG[debug] = "-Ddebug-messages=true,-Ddebug-messages=false"
PACKAGECONFIG[logging] = " -Dlogging=true,-Dlogging=false"
PACKAGECONFIG[manpages] = "-Dmanpages=true,-Dmanpages=false,scdoc-native"
PACKAGECONFIG[openssl] = "-Dopenssl=enabled,-Dopenssl=disabled,openssl"
PACKAGECONFIG[xz] = "-Dxz=enabled,-Dxz=disabled,xz"
PACKAGECONFIG[zlib] = "-Dzlib=enabled,-Dzlib=disabled,zlib"
PACKAGECONFIG[zstd] = "-Dzstd=enabled,-Dzstd=disabled,zstd"

PROVIDES += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"
RPROVIDES:${PN} += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"
RCONFLICTS:${PN} += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"
RREPLACES:${PN} += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"

# to force user to remove old module-init-tools and replace them with kmod variants
RCONFLICTS:libkmod2 += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"

do_install:append () {
        # install depmod.d file for search/ dir
        install -Dm644 "${UNPACKDIR}/depmod-search.conf" "${D}${nonarch_base_libdir}/depmod.d/search.conf"

        # Add .debug to the exclude path for depmod
        echo "exclude .debug" > ${D}${nonarch_base_libdir}/depmod.d/exclude.conf
}

PACKAGES += "${PN}-fish-completion ${PN}-zsh-completion"

FILES:${PN}-fish-completion = "${datadir}/fish"
FILES:${PN}-zsh-completion = "${datadir}/zsh"

ALTERNATIVE_PRIORITY = "70"

ALTERNATIVE:kmod = "insmod modprobe rmmod modinfo bin-lsmod lsmod depmod"

ALTERNATIVE_LINK_NAME[depmod] = "${base_sbindir}/depmod"
ALTERNATIVE_LINK_NAME[insmod] = "${base_sbindir}/insmod"
ALTERNATIVE_LINK_NAME[modprobe] = "${base_sbindir}/modprobe"
ALTERNATIVE_LINK_NAME[rmmod] = "${base_sbindir}/rmmod"
ALTERNATIVE_LINK_NAME[modinfo] = "${base_sbindir}/modinfo"
ALTERNATIVE_LINK_NAME[bin-lsmod] = "${base_sbindir}/lsmod"
ALTERNATIVE_LINK_NAME[lsmod] = "${base_sbindir}/lsmod"
ALTERNATIVE_TARGET[lsmod] = "${base_sbindir}/lsmod.${BPN}"

PACKAGES =+ "libkmod"
FILES:libkmod = "${base_libdir}/libkmod*${SOLIBS} ${libdir}/libkmod*${SOLIBS}"
FILES:${PN} += "${nonarch_base_libdir}/depmod.d ${nonarch_base_libdir}/modprobe.d"

BBCLASSEXTEND = "native nativesdk"
