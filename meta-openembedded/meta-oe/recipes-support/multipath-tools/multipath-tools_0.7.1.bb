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
           file://0001-multipath-attempt-at-common-multipath.rules.patch \
           file://0002-RH-fixup-udev-rules-for-redhat.patch \
           file://0003-RH-Remove-the-property-blacklist-exception-builtin.patch \
           file://0004-RH-don-t-start-without-a-config-file.patch \
           file://0005-RH-add-mpathconf.patch \
           file://0006-RH-add-wwids-from-kernel-cmdline-mpath.wwids-with-A.patch \
           file://0007-RH-trigger-change-uevent-on-new-device-creation.patch \
           file://0008-libmultipath-change-how-RADOS-checker-is-enabled.patch \
           file://0009-multipath-set-verbosity-to-default-during-config.patch \
           file://0010-mpath-skip-device-configs-without-vendor-product.patch \
           file://0011-multipathd-fix-show-maps-json-crash.patch \
           file://0012-multipath-tools-modify-Makefile.inc-for-cross-compil.patch \
           file://0013-Always-use-devmapper.patch \
           file://0014-Always-use-devmapper-for-kpartx.patch \
           file://0001-kpartx-include-limits.h-for-PATH_MAX.patch \
           "
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

SRCREV = "f21166a812a2cfb50ecf9550d32947c83103f83a"

S = "${WORKDIR}/git"

inherit systemd pkgconfig

SYSTEMD_SERVICE_${PN} = "multipathd.service"

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
                ${nonarch_libdir}/udev/kpartx_id \
               "

RDEPENDS_${PN} += "bash kpartx"
