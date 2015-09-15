SUMMARY = "Creates an 'xuser' account used for running X11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = ""

inherit allarch useradd

do_configure() {
    :
}

do_compile() {
    :
}

do_install() {
    :
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system shutdown"
USERADD_PARAM_${PN} = "--create-home \
                       --groups video,tty,audio,input,shutdown,disk \
                       --user-group xuser"

ALLOW_EMPTY_${PN} = "1"
