SUMMARY = "The upstream project used to drive the Device Mapper multipathing driver"

DESCRIPTION = "It provides tools to manage multipath devices \
by instructing the device-mapper kernel module what to do. These \
tools include: \
1. multipath - Scan the system for multipath devices and assemble them.\
2. multipathd - Detects when paths fail and execs multipath to update \
things.\
3. mpathpersist - Persistent reservation management feature allows \
cluster management software to manage persistent reservation through \
mpath device. It processes management requests from callers and hides \
the management task details. It also handles persistent reservation \
management of data path life cycle and state changes.\
4. kpartx - This tool, derived from util-linux's partx, reads partition \
tables on specified device and create device maps over partitions \
segments detected. It is called from hotplug upon device maps creation \
and deletion"

HOMEPAGE = "http://christophe.varoqui.free.fr/"

DEPENDS = "libdevmapper \
           lvm2 \
           libaio \
           liburcu \
           readline \
           udev \
           json-c \
          "

LICENSE = "GPL-2.0-only"

SRC_URI = "git://github.com/opensvc/multipath-tools.git;protocol=https;branch=master \
           file://multipathd.oe \
           file://multipath.conf.example \
           file://0001-RH-fixup-udev-rules-for-redhat.patch \
           file://0002-RH-Remove-the-property-blacklist-exception-builtin.patch \
           file://0003-RH-don-t-start-without-a-config-file.patch \
           file://0004-RH-use-rpm-optflags-if-present.patch \
           file://0005-RH-add-mpathconf.patch \
           file://0006-RH-add-wwids-from-kernel-cmdline-mpath.wwids-with-A.patch \
           file://0007-RH-warn-on-invalid-regex-instead-of-failing.patch \
           file://0008-RH-reset-default-find_mutipaths-value-to-off.patch \
           file://0009-multipath-tools-modify-create-config.mk-for-cross-co.patch \
           file://0010-Always-use-devmapper.patch \
           file://0011-fix-bug-of-do_compile-and-do_install.patch \
           file://0012-add-explicit-dependency-on-libraries.patch \
           "

LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

SRCREV = "ee3a70175a8a9045e5c309d5392300922e2a0625"

S = "${WORKDIR}/git"

inherit systemd pkgconfig

SYSTEMD_SERVICE:${PN} = "multipathd.service"
SYSTEMD_AUTO_ENABLE = "disable"

TARGET_CC_ARCH += "${LDFLAGS}"

# multipath-tools includes a copy of the valgrind.h header
# file and uses the macros to suppress some false positives. However,
# that only works on ARM when thumb is disabled. Otherwise one gets:
#   Error: shifts in CMP/MOV instructions are only supported in unified syntax -- `mov r12,r12,ror#3'
#   ../Makefile.inc:66: recipe for target 'debug.o' failed
ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"

# The exact version of SYSTEMD does not matter but should be greater than 209.
#
EXTRA_OEMAKE = 'MULTIPATH_VERSION=${PV} DESTDIR=${D} syslibdir=${base_libdir} \
                OPTFLAGS="${CFLAGS}" \
                prefix=${prefix} \
                etc_prefix=${sysconfdir} \
                bindir=${base_sbindir} \
                LIB=${base_libdir} libdir=${base_libdir}/multipath \
                usrlibdir=${libdir} \
                plugindir=${base_libdir}/multipath \
                unitdir=${systemd_system_unitdir} \
                libudevdir=${nonarch_base_libdir}/udev \
                modulesloaddir=${sysconfdir}/modules-load.d \
                tmpfilesdir=${sysconfdir}/tmpfiles.d \
                ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "SYSTEMD=216", "", d)} \
               '

do_install() {
    oe_runmake install

    # We copy an initscript, but do not start multipathd at init time.
    #
    if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)};then
        install -d ${D}${sysconfdir}/init.d
        cp ${UNPACKDIR}/multipathd.oe ${D}${sysconfdir}/init.d/multipathd
    fi

    sed -i "s:/usr/lib/udev/kpartx_id:${nonarch_base_libdir}/udev/kpartx_id:g" \
        ${D}${nonarch_base_libdir}/udev/rules.d/11-dm-mpath.rules

    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/multipath.conf.example \
    ${D}${sysconfdir}/multipath.conf.example
}

FILES:${PN} += "${systemd_system_unitdir}"
FILES:${PN}-dbg += "${base_libdir}/multipath/.debug"

PACKAGES =+ "${PN}-libs"
FILES:${PN}-libs = "${base_libdir}/lib*.so.* \
                    ${base_libdir}/multipath/lib*.so*"
RDEPENDS:${PN} += "${PN}-libs bash libgcc"

PROVIDES += "device-mapper-multipath"
RPROVIDES:${PN} += "device-mapper-multipath"
RPROVIDES:${PN}-libs += "device-mapper-multipath-libs"

FILES:${PN}-dev += "${base_libdir}/pkgconfig"

PACKAGES =+ "kpartx"
FILES:kpartx = "${base_sbindir}/kpartx \
                ${nonarch_base_libdir}/udev/kpartx_id \
               "

RDEPENDS:${PN} += "kpartx"
PARALLEL_MAKE = ""
