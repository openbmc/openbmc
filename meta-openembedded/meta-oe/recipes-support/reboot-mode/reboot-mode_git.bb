SUMMARY = "Reboot the device to a specific mode."

LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=84dcc94da3adb52b53ae4fa38fe49e5d"

SRC_URI = "git://gitlab.com/postmarketOS/reboot-mode.git;protocol=http;branch=master"
SRCREV = "84831b20512abd9033414ca5f5a023f333525335"

S = "${WORKDIR}/git"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} ${S}/reboot-mode.c -o ${B}/reboot-mode
}

do_install() {
    install -D -m 0755 ${B}/reboot-mode ${D}${bindir}/reboot-mode
}
