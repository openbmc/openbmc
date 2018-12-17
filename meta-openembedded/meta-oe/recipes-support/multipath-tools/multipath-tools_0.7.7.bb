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
           file://0001-multipath-tools-add-RDAC-SUN-ArrayStorage-to-hwtable.patch \
           file://0002-multipath-tools-remove-c-from-__cpluscplus-misspelle.patch \
           file://0003-multipath-tools-remove-emacs-autoconfig-of-kpartx-gp.patch \
           file://0004-multipath-tools-replace-FSF-address-with-a-www-point.patch \
           file://0005-multipath-tools-Remove-trailing-leading-whitespaces-.patch \
           file://0006-multipath-tools-fix-compilation-with-musl-libc.patch \
           file://0007-multipath-tools-add-x-to-doc-preclean.pl-and-split-m.patch \
           file://0008-multipath-tools-refresh-kernel-doc-from-kernel-sourc.patch \
           file://0009-multipath-tools-configure-hitachi-ams2000-and-hus100.patch \
           file://0010-libmultipath-don-t-reject-maps-with-undefined-prio.patch \
           file://0011-multipathd-handle-errors-in-uxlsnr-as-fatal.patch \
           file://0012-libmultipath-fix-error-parsing-find_multipaths-stric.patch \
           file://0013-libmultipath-print-correct-default-for-delay_-_check.patch \
           file://0014-multipath.conf.5-clarify-property-whitelist-handling.patch \
           file://0015-mpathpersist-add-all_tg_pt-option.patch \
           file://0016-libmultipath-remove-rbd-code.patch \
           file://0017-mpathpersist-fix-aptpl-support.patch \
           file://0018-multipath-don-t-check-timestamps-without-a-path.patch \
           file://0019-libmultipath-fix-detect-alua-corner-case.patch \
           file://0020-multipath-fix-setting-conf-version.patch \
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
           "

LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

SRCREV = "386d288b5595fc2c01dffe698b6eb306c6674908"

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
