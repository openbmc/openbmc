SUMARRY = "The ethernet monitor program; for keeping track of ethernet/ip address pairings"
LICENSE = "BSD-4-Clause"
HOME_PAGE = "http://ee.lbl.gov/"
LIC_FILES_CHKSUM = "file://configure;md5=0f6cca2f69f384a14e2f5803210ca92e"

DEPENDS += "libpcap"

SRC_URI = "https://ee.lbl.gov/downloads/arpwatch/${BP}.tar.gz \
           file://arpwatch.conf \
           file://arpwatch.default \
           file://arpwatch_init  \
           file://host_contam_fix.patch \
           "

SRC_URI[sha256sum] = "d47fa8b291fc37a25a2d0f3e1b64f451dc0be82d714a10ffa6ef8b0b9e33e166"

inherit  autotools-brokensep update-rc.d useradd

ARPWATCH_UID ?= "arpwatch"
ARPWATCH_GID ?= "arpwatch"
APRWATCH_FROM ?= "root "
ARPWATH_REPLY ?= "${ARPWATCH_UID}"

# many configure tests are failing with gcc-14
CFLAGS += "-Wno-error=implicit-int -Wno-error=implicit-function-declaration"
BUILD_CFLAGS += "-Wno-error=implicit-int -Wno-error=implicit-function-declaration"

PACKAGECONFIG ??= ""

PACKAGECONFIG[email] = "-with-watcher=email=${APRWATCH_FROM} --with-watchee=email=${ARPWATH_REPLY}, , postfix, postfix postfix-cfg"

CONFIGUREOPTS = " --build=${BUILD_SYS} \
          --host=${HOST_SYS} \
          --target=${TARGET_SYS} \
          --prefix=${prefix} \
          --exec_prefix=${exec_prefix} \
          --bindir=${bindir} \
          --sbindir=${sbindir} \
          --libexecdir=${libexecdir} \
          --datadir=${datadir} \
          --sysconfdir=${sysconfdir} \
          --sharedstatedir=${sharedstatedir} \
          --localstatedir=${localstatedir} \
          --libdir=${libdir} \
          --includedir=${includedir} \
          --infodir=${infodir} \
          --mandir=${mandir} \
          --srcdir=${S} \
          --with-sendmail=${sbindir}/sendmail \
          "

do_configure () {
    ${S}/configure ${CONFIGUREOPTS}
}

do_install () {
    install -d ${D}${bindir}
    install -d ${D}${sbindir}
    install -d ${D}${mandir}/man8
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/default
    install -d ${D}${sysconfdir}/init.d
    install -d ${D}${prefix}/etc/rc.d
    install -d ${D}/var/lib/arpwatch

    oe_runmake install DESTDIR=${D}
    install -m 644 ${UNPACKDIR}/arpwatch.conf  ${D}${sysconfdir}
    install -m 655 ${UNPACKDIR}/arpwatch_init  ${D}${sysconfdir}/init.d/arpwatch
    install -m 644 ${UNPACKDIR}/arpwatch.default  ${D}${sysconfdir}/default
}

INITSCRIPT_NAME = "arpwatch"
INITSCRIPT_PARAMS = "start 02 2 3 4 5 . stop 20 0 1 6 ."

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system ${ARPWATCH_UID}"
USERADD_PARAM:${PN} = "--system -g ${ARPWATCH_GID} --home-dir  \
    ${localstatedir}/spool/${BPN} \
    --no-create-home  --shell /bin/false ${BPN}"

CONFFILE_FILES = "${sysconfdir}/${PN}.conf"

FILES:${PN} = "${bindir} ${sbindir} ${prefix}/etc/rc.d \
               ${sysconfdir} /var/lib/arpwatch"

COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:riscv64 = "null"
COMPATIBLE_HOST:libc-musl = "null"

RDEPENDS:${PN} = "libpcap"
