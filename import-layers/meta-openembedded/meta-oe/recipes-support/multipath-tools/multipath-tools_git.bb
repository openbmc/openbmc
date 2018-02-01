SUMMARY = "The upstream project used to drive the Device Mapper multipathing driver"

DEPENDS = "libdevmapper libaio liburcu readline udev"

LICENSE = "LGPLv2"

SRC_URI = "git://git.opensvc.com/multipath-tools/.git;protocol=http \
           file://multipathd.oe \
           file://makefile_inc.patch \
           file://always-use-libdevmapper.patch \
           file://always-use-libdevmapper-kpartx.patch \
           file://0001-multipathd.service-Error-fix.patch \
           file://shared-libs-avoid-linking-.so-as-executable.patch \
           file://checkers-disable-libcheckrbd.so.patch \
           "
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

# 0.6.4
SRCREV = "922421cf795d53d339862bb2d99f1a85c96ad9a3"

inherit systemd


S = "${WORKDIR}/git"

PV = "0.6.4+git${@'${SRCPV}'.split('+')[-1]}"

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
    install -d ${D}${sysconfdir}/init.d
    cp ${WORKDIR}/multipathd.oe ${D}${sysconfdir}/init.d/multipathd

}

FILES_${PN}-dbg += "${base_libdir}/multipath/.debug"

# systemd and udev stuff always goes under /lib!
#
FILES_${PN} += "${base_libdir}/multipath \
                ${systemd_system_unitdir} \
		"

PACKAGES =+ "kpartx"
FILES_kpartx = "${base_sbindir}/kpartx \
                ${nonarch_libdir}/udev/kpartx_id \
               "

RDEPENDS_${PN} += "kpartx"
