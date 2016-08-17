SUMMARY = "Configuration files for online package repositories aka feeds"
PR = "r6"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

DISTRO_FEED_PREFIX ?= "remote"
DISTRO_FEED_URI ?= "http://my-distribution.example/remote-feed/"
DISTRO_FEED_ARCHS ?= "all ${PACKAGE_EXTRA_ARCHS} ${MACHINE_ARCH}"

do_compile() {
    mkdir -p ${S}/${sysconfdir}/opkg
    for feed in ${DISTRO_FEED_ARCHS}; do
        echo "src/gz ${DISTRO_FEED_PREFIX}-${feed} ${DISTRO_FEED_URI}/${feed}" > ${S}/${sysconfdir}/opkg/${feed}-feed.conf
    done
}
do_install () {
    install -d ${D}${sysconfdir}/opkg
    install -m 0644 ${S}/${sysconfdir}/opkg/* ${D}${sysconfdir}/opkg/
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

#def distro_feed_configs(d):
#    import bb
#    parchs = d.getVar( "PACKAGE_EXTRA_ARCHS", 1 ).split()
#    march = d.getVar( "MACHINE_ARCH", 1 ).split()
#    archs = [ "all" ] + parchs + march
#    confs = [ ( "${sysconfdir}/opkg/%s-feed.conf" % feed ) for feed in archs ]
#    return " ".join( confs )
#
#CONFFILES_${PN} += '${@distro_feed_configs(d)}'

CONFFILES_${PN} += '${@ " ".join( [ ( "${sysconfdir}/opkg/%s-feed.conf" % feed ) for feed in "all ${PACKAGE_EXTRA_ARCHS} ${MACHINE_ARCH}".split() ] ) }'
