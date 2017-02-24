SUMMARY = "The upstream project used to drive the Device Mapper multipathing driver"

DEPENDS = "lvm2 libaio readline udev"

LICENSE = "LGPLv2"

SRC_URI = "git://git.opensvc.com/multipath-tools/.git;protocol=http \
           file://multipathd.oe \
           file://makefile_inc.patch \
           file://always-use-libdevmapper.patch \
           file://always-use-libdevmapper-kpartx.patch \
           file://do-not-link-libmpathpersist-to-TMPDIR.patch \
           file://0001-multipathd.service-Error-fix.patch \
           "
# 0.5.0
#
#SRCREV = "82f391e787dc02e9d9294aa391137ab424bb83c4"
#LIC_FILES_CHKSUM = "file://COPYING;md5=7be2873b6270e45abacc503abbe2aa3d"

# 0.5.0 + commits thru 7/18/2014
#
#SRCREV = "0d72f46c12207a6b7b89f5ef4f5ab5f87ed8bc90"
#LIC_FILES_CHKSUM = "file://COPYING;md5=b06690e7a95c166eefe0199b39118eb1"

# 0.5.0 + commits thru 9/12/2014
#
#    includes important systemd related structure size fix
#
#SRCREV = "aec68ab217fd2956443b27ceeb97dd6475267789"
LIC_FILES_CHKSUM = "file://COPYING;md5=b06690e7a95c166eefe0199b39118eb1"

# 0.5.0 + commits thru 2/16/2015
SRCREV = "770e6d0da035deaced82885939161c2b69225e10"

inherit systemd


S = "${WORKDIR}/git"

PV = "0.5.0+git${@'${SRCPV}'.split('+')[-1]}"

TARGET_CC_ARCH += "${LDFLAGS}"

# The exact version of SYSTEMD does not matter but should be greater than 209.
#
EXTRA_OEMAKE = 'MULTIPATH_VERSION=${PV} DESTDIR=${D} syslibdir=${base_libdir} \
                OPTFLAGS="${CFLAGS}" \
                LIB=${base_libdir} libdir=${base_libdir}/multipath \
                unitdir=/lib/systemd/system \
                ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "SYSTEMD=216", "", d)} \
               '

do_install() {
    oe_runmake install

    # Copy a sample conf file, but do not rename it multipath.conf.
    #
    cp multipath.conf.defaults ${D}${sysconfdir}

    # We copy an initscript, but do not start multipathd at init time.
    #
    cp ${WORKDIR}/multipathd.oe ${D}${sysconfdir}/init.d/multipathd

}

FILES_${PN}-dbg += "${base_libdir}/multipath/.debug"

# systemd and udev stuff always goes under /lib!
#
FILES_${PN} += "${base_libdir}/multipath \
                /lib/systemd"

PACKAGES =+ "kpartx"
FILES_kpartx = "${base_sbindir}/kpartx \
                /usr/lib/udev/kpartx_id \
               "

RDEPENDS_${PN} += "kpartx"
