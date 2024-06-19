SUMMARY = "Creates an 'xuser' account used for running X11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://system-xuser.conf"

inherit allarch useradd

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    install -D -m 0644 ${UNPACKDIR}/system-xuser.conf ${D}${sysconfdir}/dbus-1/system.d/system-xuser.conf
}

FILES:${PN} = "${sysconfdir}/dbus-1/system.d/system-xuser.conf"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--create-home \
                       --groups video,tty,audio,input,shutdown,disk \
                       --user-group xuser"

ALLOW_EMPTY:${PN} = "1"
