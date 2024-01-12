SUMMARY = "Mycroft is a hackable open source voice assistant."
DESCRIPTION = "Mycroft is the world’s first open source assistant. "
HOMEPAGE = "https://mycroft.ai/"
SECTION = "multimedia"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=79aa497b11564d1d419ee889e7b498f6"

SRCREV = "913f29d3d550637934f9abf43a097eb2c30d76fc"
SRC_URI = "git://github.com/MycroftAI/mycroft-core.git;branch=master;protocol=https \
           file://0001-Remove-python-venv.patch \
           file://0002-dev_setup.sh-Remove-the-git-dependency.patch \
           file://0003-dev_setup.sh-Remove-the-TERM-dependency.patch \
           file://0004-dev_setup.sh-Ignore-missing-package-manager.patch \
           file://0005-pip-requirements-Don-t-install-requirements-with-pip.patch \
           file://0006-Use-python3-and-pip3-instead-of-python-and-pip.patch \
           file://0007-mycroft.conf-Use-pocketsphinx-by-default.patch \
           file://dev_opts.json \
           file://mycroft-setup.service \
           file://mycroft.service \
          "

S = "${WORKDIR}/git"

inherit systemd features_check

# Mycroft installs itself on the host
# Just copy the setup files to the rootfs
# The mycroft-setup service will copy the files to /var/ where we run them from
do_install() {
    install -d ${D}${libdir}/
    cp -r ${B} ${D}${libdir}/mycroft
    rm -r ${D}${libdir}/mycroft/.git

    # Install the dev opts so it doesn't ask us on initial setup.
    install -m 644 ${WORKDIR}/dev_opts.json ${D}${libdir}/mycroft/.dev_opts.json

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/mycroft-setup.service ${D}${systemd_unitdir}/system
        sed -i -e 's,@LIBDIR@,${libdir},g' ${D}${systemd_unitdir}/system/mycroft-setup.service
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 644 ${WORKDIR}/mycroft.service ${D}${systemd_unitdir}/system
        sed -i -e 's,@LIBDIR@,${libdir},g' ${D}${systemd_unitdir}/system/mycroft.service
    fi
}

FILES:${PN} += "${libdir}/mycroft"

RDEPENDS:${PN} = "python3"

RDEPENDS:${PN} += "python3-requests python3-pillow \
                   python3-tornado python3-pyyaml \
                   python3-pyalsaaudio python3-inflection \
                   python3-pyserial python3-psutil \
                   python3-pyaudio python3-fann2 \
                   python3-pocketsphinx \
                   python3-xxhash python3-pako \
                   python3-six python3-cryptography \
                   python3-requests-futures \
                   python3-fasteners \
                   python3-python-vlc \
                   python3-padatious python3-padaos \
                   python3-petact python3-precise-runner \
                   python3-pulsectl python3-pychromecast \
                   python3-msm python3-msk \
                   python3-websocket-client \
                   python3-google-api-python-client \
                 "

# These packages need to be installed on the target
# python3-speechrecognition python3-pyee==5.0.0 python3-six==1.10.0
# python3-websocket-client==0.54.0 python3-gtts python3-gtts-token
# python3-python-dateutil python3-adapt-parser python3-lazy

# Mycroft uses Alsa, PulseAudio and Flac
RDEPENDS:${PN} += "alsa-utils alsa-plugins alsa-tools"
RDEPENDS:${PN} += "pulseaudio pulseaudio-misc pulseaudio-server"
RDEPENDS:${PN} += "flac mpg123"

# Mycroft can do this itself on the target, but it's quicker to do it here
RDEPENDS:${PN} += "mimic"

# pgrep is used by stop-mycroft.sh
RDEPENDS:${PN} += "procps"

# More tools needed by scripts
RDEPENDS:${PN} += "bash jq libnotify"

SYSTEMD_SERVICE:${PN} = "mycroft-setup.service mycroft.service"

REQUIRED_DISTRO_FEATURES += "pulseaudio"
