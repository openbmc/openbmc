DESCRIPTION = "MiniDLNA (aka ReadyDLNA) is server software with the aim of \
being fully compliant with DLNA/UPnP-AV clients."
LICENSE = "GPL-2.0|BSD"
DEPENDS = "ffmpeg flac libav jpeg sqlite3 libexif libogg libid3tag libvorbis"

# because it depends on libav which has commercial flag
LICENSE_FLAGS = "commercial"

inherit gettext autotools-brokensep update-rc.d systemd

SRC_URI = "git://git.code.sf.net/p/minidlna/git;branch=master;module=git \
           file://minidlna-daemon.init.d \
           file://minidlna.service \
           file://0001-Update-Gettext-version.patch \
           file://0001-configure-Check-for-clock_gettime-seprately-from-__N.patch \
           "

S = "${WORKDIR}/git"

# This remove "--exclude=autopoint" option from autoreconf argument to avoid
# configure.ac:30: error: required file './ABOUT-NLS' not found
EXTRA_AUTORECONF = ""

do_install_append(){
    install -d ${D}${sysconfdir}
    install -m 0755 minidlna.conf ${D}${sysconfdir}

# Systemd script
    install -d ${D}${nonarch_base_libdir}/systemd/system
    install -m 0755 ${WORKDIR}/minidlna.service ${D}${nonarch_base_libdir}/systemd/system

# Sysvinit script
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/minidlna-daemon.init.d ${D}${sysconfdir}/init.d/minidlna

}

SYSTEMD_SERVICE_${PN} = "minidlna.service"

INITSCRIPT_NAME = "minidlna"
INITSCRIPT_PARAMS = "defaults 90"

