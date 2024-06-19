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
inherit autotools bash-completion gtk-doc pkgconfig manpages update-alternatives

SRCREV = "41faa59711742c1476d59985011ee0f27ed91d30"

SRC_URI = "git://git.kernel.org/pub/scm/utils/kernel/kmod/kmod.git;branch=master;protocol=https \
           file://depmod-search.conf \
           file://avoid_parallel_tests.patch \
           file://0001-Use-portable-implementation-for-basename-API.patch \
           file://gtkdocdir.patch \
           "

S = "${WORKDIR}/git"

EXTRA_OECONF += "--enable-tools"

PACKAGECONFIG ??= "zlib xz openssl"
PACKAGECONFIG[debug] = "--enable-debug,--disable-debug"
PACKAGECONFIG[logging] = " --enable-logging,--disable-logging"
PACKAGECONFIG[manpages] = "--enable-manpages, --disable-manpages, libxslt-native xmlto-native"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl"
PACKAGECONFIG[xz] = "--with-xz,--without-xz,xz"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"
PACKAGECONFIG[zstd] = "--with-zstd,--without-zstd,zstd"

PROVIDES += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"
RPROVIDES:${PN} += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"
RCONFLICTS:${PN} += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"
RREPLACES:${PN} += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"

# to force user to remove old module-init-tools and replace them with kmod variants
RCONFLICTS:libkmod2 += "module-init-tools-insmod-static module-init-tools-depmod module-init-tools"

# autotools set prefix to /usr, however we want them in /bin and /sbin
EXTRA_OECONF += "--bindir=${base_bindir} --sbindir=${base_sbindir}"

do_install:append () {
        install -dm755 ${D}${base_bindir}
        install -dm755 ${D}${base_sbindir}
        # add symlinks to kmod
        [ -e ${D}${base_bindir}/lsmod ] || ln -rs ${D}${base_bindir}/kmod ${D}${base_bindir}/lsmod
        for tool in insmod rmmod depmod modinfo modprobe; do
                rm -f ${D}${base_bindir}/${tool}
                ln -rs ${D}${base_bindir}/kmod ${D}${base_sbindir}/${tool}
        done
        # configuration directories
        install -dm755 ${D}${nonarch_base_libdir}/depmod.d
        install -dm755 ${D}${nonarch_base_libdir}/modprobe.d
        install -dm755 ${D}${sysconfdir}/depmod.d
        install -dm755 ${D}${sysconfdir}/modprobe.d

        # install depmod.d file for search/ dir
        install -Dm644 "${UNPACKDIR}/depmod-search.conf" "${D}${nonarch_base_libdir}/depmod.d/search.conf"

        # Add .debug to the exclude path for depmod
        echo "exclude .debug" > ${D}${nonarch_base_libdir}/depmod.d/exclude.conf
}

ALTERNATIVE_PRIORITY = "70"

ALTERNATIVE:kmod = "insmod modprobe rmmod modinfo bin-lsmod lsmod depmod"

ALTERNATIVE_LINK_NAME[depmod] = "${base_sbindir}/depmod"
ALTERNATIVE_LINK_NAME[insmod] = "${base_sbindir}/insmod"
ALTERNATIVE_LINK_NAME[modprobe] = "${base_sbindir}/modprobe"
ALTERNATIVE_LINK_NAME[rmmod] = "${base_sbindir}/rmmod"
ALTERNATIVE_LINK_NAME[modinfo] = "${base_sbindir}/modinfo"
ALTERNATIVE_LINK_NAME[bin-lsmod] = "${base_bindir}/lsmod"
ALTERNATIVE_LINK_NAME[lsmod] = "${base_sbindir}/lsmod"
ALTERNATIVE_TARGET[lsmod] = "${base_bindir}/lsmod.${BPN}"

PACKAGES =+ "libkmod"
FILES:libkmod = "${base_libdir}/libkmod*${SOLIBS} ${libdir}/libkmod*${SOLIBS}"
FILES:${PN} += "${nonarch_base_libdir}/depmod.d ${nonarch_base_libdir}/modprobe.d"

BBCLASSEXTEND = "native nativesdk"
