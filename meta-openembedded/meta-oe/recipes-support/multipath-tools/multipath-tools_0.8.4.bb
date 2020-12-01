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

LICENSE = "GPLv2"

SRC_URI = "git://git.opensvc.com/multipath-tools/.git;protocol=http \
           file://multipathd.oe \
           file://multipath.conf.example \
           file://0021-RH-fixup-udev-rules-for-redhat.patch \
           file://0022-RH-Remove-the-property-blacklist-exception-builtin.patch \
           file://0023-RH-don-t-start-without-a-config-file.patch \
           file://0024-RH-use-rpm-optflags-if-present.patch \
           file://0025-RH-add-mpathconf.patch \
           file://0026-RH-add-wwids-from-kernel-cmdline-mpath.wwids-with-A.patch \
           file://0027-RH-warn-on-invalid-regex-instead-of-failing.patch \
           file://0028-RH-reset-default-find_mutipaths-value-to-off.patch \
           file://0029-multipath-tools-modify-Makefile.inc-for-cross-compil.patch \
           file://0030-Always-use-devmapper.patch \
           file://0031-Always-use-devmapper-for-kpartx.patch \
           file://0001-fix-bug-of-do_compile-and-do_install.patch \
           file://0001-add-explicit-dependency-on-libraries.patch \
           "

LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

SRCREV = "d4915917655b3d205aa0e339ca13080ed8182d0d"

S = "${WORKDIR}/git"

inherit systemd pkgconfig

SYSTEMD_SERVICE_${PN} = "multipathd.service"
SYSTEMD_AUTO_ENABLE = "disable"

TARGET_CC_ARCH += "${LDFLAGS}"

# multipath-tools includes a copy of the valgrind.h header
# file and uses the macros to suppress some false positives. However,
# that only works on ARM when thumb is disabled. Otherwise one gets:
#   Error: shifts in CMP/MOV instructions are only supported in unified syntax -- `mov r12,r12,ror#3'
#   ../Makefile.inc:66: recipe for target 'debug.o' failed
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"

# The exact version of SYSTEMD does not matter but should be greater than 209.
#
EXTRA_OEMAKE = 'MULTIPATH_VERSION=${PV} DESTDIR=${D} syslibdir=${base_libdir} \
                OPTFLAGS="${CFLAGS}" \
                bindir=${base_sbindir} \
                LIB=${base_libdir} libdir=${base_libdir}/multipath \
                unitdir=${systemd_system_unitdir} \
                libudevdir=${nonarch_base_libdir}/udev \
                ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "SYSTEMD=216", "", d)} \
               '

do_install() {
    oe_runmake install

    # We copy an initscript, but do not start multipathd at init time.
    #
    if ${@bb.utils.contains('DISTRO_FEATURES','sysvinit','true','false',d)};then
        install -d ${D}${sysconfdir}/init.d
        cp ${WORKDIR}/multipathd.oe ${D}${sysconfdir}/init.d/multipathd
    fi

    sed -i "s:/usr/lib/udev/kpartx_id:${nonarch_base_libdir}/udev/kpartx_id:g" \
        ${D}${nonarch_base_libdir}/udev/rules.d/11-dm-mpath.rules

    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/multipath.conf.example \
    ${D}${sysconfdir}/multipath.conf.example
}

FILES_${PN}-dbg += "${base_libdir}/multipath/.debug"

PACKAGES =+ "${PN}-libs"
FILES_${PN}-libs = "${base_libdir}/lib*.so.* \
                    ${base_libdir}/multipath/lib*.so*"
RDEPENDS_${PN} += "${PN}-libs bash"

PROVIDES += "device-mapper-multipath"
RPROVIDES_${PN} += "device-mapper-multipath"
RPROVIDES_${PN}-libs += "device-mapper-multipath-libs"

FILES_${PN}-dev += "${base_libdir}/pkgconfig"

PACKAGES =+ "kpartx"
FILES_kpartx = "${base_sbindir}/kpartx \
                ${nonarch_base_libdir}/udev/kpartx_id \
               "

RDEPENDS_${PN} += "kpartx"
PARALLEL_MAKE = ""
