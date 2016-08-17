SUMMARY = "Apcupsd a daemon for controlling APC UPSes"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=c12853cc7fdf20d17b4fddefd26b7802"

SRC_URI = "http://garr.dl.sourceforge.net/project/apcupsd/apcupsd%20-%20Stable/3.14.10/apcupsd-${PV}.tar.gz"
SRC_URI[md5sum] = "5928822d855c5cf7ac29655e3e0b8c23"
SRC_URI[sha256sum] = "0707b5ec9916fbde9e44eb8d18037c8d8f75dfd6aeef51aba5487e189eef2032"

PNBLACKLIST[apcupsd] ?= "BROKEN: doesn't build with B!=S"

inherit autotools

LD = "${CXX}"

EXTRA_OECONF = "--without-x \
                --enable-usb \
                --with-distname=${DISTRO}"

do_configure() {
    export topdir=${S}
    cp -R --no-dereference --preserve=mode,links -v ${S}/autoconf/configure.in ${S}

    if ! [ -d ${S}/platforms/${DISTRO} ] ; then
        cp -R --no-dereference --preserve=mode,links -v ${S}/platforms/unknown ${S}/platforms/${DISTRO} 
    fi

    gnu-configize --force
    # install --help says '-c' is an ignored option, but it turns out that the argument to -c isn't ignored, so drop the complete '-c path/to/strip' line
    sed -i -e 's:$(INSTALL_PROGRAM) $(STRIP):$(INSTALL_PROGRAM):g' ${S}/autoconf/targets.mak
    # Searching in host dirs triggers the QA checks
    sed -i -e 's:-I/usr/local/include::g' -e 's:-L/usr/local/lib64::g' -e 's:-L/usr/local/lib::g' ${S}/configure

    # m4 macros are missing, using autotools_do_configure leads to linking errors with gethostname_re
    oe_runconf
}

do_install_append() {
    rm ${D}${datadir}/hal -rf
}


