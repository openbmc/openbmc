DESCRIPTION = "A real-time adjustable equalizer plugin for ALSA"
HOMEPAGE = "https://web.archive.org/web/20161105202833/http://thedigitalmachine.net/alsaequal.html"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

DEPENDS = "alsa-lib"

SRC_URI = " \
    https://launchpad.net/ubuntu/+archive/primary/+files/alsaequal_${PV}.orig.tar.bz2 \
    file://0001-Fix-asneeded.patch \
    file://0002-Fix-Eq-CAPS-plugin-name.patch \
    file://0003-Fix-mixer.patch \
"

SRC_URI[md5sum] = "d2edc7710c72cbf3ab297c414e35ebda"
SRC_URI[sha256sum] = "916e7d152added24617efc350142438a46099efe062bd8781d36dbf10b4e6ff0"

S = "${WORKDIR}/alsaequal"

EXTRA_OEMAKE = " \
    Q='' \
    CC='${CC}' \
    LD='${CC}' \
    LDFLAGS='${LDFLAGS} -shared -lasound' \
    CFLAGS='${CFLAGS} -I. -funroll-loops -ffast-math -fPIC -DPIC' \
    SND_PCM_LIBS='-lasound' \
    SND_CTL_LIBS='-lasound' \
"

do_compile() {
    oe_runmake all
}

do_install() {
    install -Dm 0644 libasound_module_ctl_equal.so ${D}${libdir}/alsa-lib/libasound_module_ctl_equal.so
    install -Dm 0644 libasound_module_pcm_equal.so ${D}${libdir}/alsa-lib/libasound_module_pcm_equal.so
}

RDEPENDS_${PN} += " \
    alsa-utils \
    caps \
"

FILES_${PN} = "${libdir}/alsa-lib/"
