DESCRIPTION = "CTDB is a cluster implementation of the TDB database \
used by Samba and other projects to store temporary data. If an \
application is already using TDB for temporary data it is very easy \
to convert that application to be cluster aware and use CTDB instead."
HOMEPAGE = "https://ctdb.samba.org/"
LICENSE = "GPL-2.0+ & LGPL-3.0+ & GPL-3.0+"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://${COREBASE}/meta/files/common-licenses/LGPL-3.0;md5=bfccfe952269fff2b407dd11f2f3083b \
                    file://${COREBASE}/meta/files/common-licenses/GPL-2.0;md5=801f80980d171dd6425610833a22dbe6 \
                    "

SRC_URI = "https://ftp.samba.org/pub/${BPN}/${BP}.tar.gz \
           file://01-support-cross-compile-for-linux-os.patch \
           file://02-link-rep_snprintf-for-ltdbtool.patch \
           file://service-ensure-the-PID-directory-is-created.patch \
          "

SRC_URI[md5sum] = "efc50109f90a4c790124c35e4ba234ad"
SRC_URI[sha256sum] = "fc1cdd7efcc91afc629f21408281bba5da107b9c17e74bdee2f28a532d14f874"

inherit autotools-brokensep pkgconfig systemd

PACKAGECONFIG ??= ""
PACKAGECONFIG[libtdb] = "--without-included-tdb,--with-included-tdb,libtdb"

PARALLEL_MAKE = ""

DEPENDS += "popt libtevent libtalloc libldb"
DEPENDS_append_libc-musl = " libtirpc"
CFLAGS_append_libc-musl = " -I${STAGING_INCDIR}/tirpc"
LDFLAGS_append_libc-musl = " -ltirpc"

# ctdbd_wrapper requires pgrep, hence procps
RDEPENDS_${PN} += "procps"

do_configure() {
    oe_runconf
}

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${S}/config/ctdb.service ${D}${systemd_unitdir}/system
    sed -i -e 's,/usr/sbin/,${sbindir}/,' ${D}${systemd_unitdir}/system/ctdb.service
    sed -i -e 's,\([=\ ]\)/run/,\1${localstatedir}/run/,' ${D}${systemd_unitdir}/system/ctdb.service

    rm -r ${D}/${localstatedir}/run
}

# The systemd service is disabled by default, as the service will fail to
# start without /etc/ctdb/nodes. If the user supplies this, they can re-enable
# the service.
SYSTEMD_AUTO_ENABLE = "disable"
SYSTEMD_SERVICE_${PN} = "ctdb.service"

# onnode is a shell script with bashisms and bash #!
RDEPENDS_${PN} += "bash"
