SUMMARY = "Mycroft is a hackable open source voice assistant."
DESCRIPTION = "Mycroft is the world’s first open source assistant. "
HOMEPAGE = "https://mycroft.ai/"
SECTION = "multimedia"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=79aa497b11564d1d419ee889e7b498f6"

SRCREV = "4b45db34ecd95b62ef2b66a8e5180c66ca791a21"
SRC_URI = "git://github.com/MycroftAI/mycroft-core.git;branch=master \
           file://0001-Remove-python-venv.patch \
           file://0002-pip-requirements-Remove-ones-installed-by-OE.patch \
           file://0003-Use-python3-and-pip3-instead-of-python-and-pip.patch \
           file://0004-dev_setup.sh-Remove-the-git-dependency.patch \
           file://0005-dev_setup.sh-Remove-the-test-setup-dependency.patch \
           file://dev_opts.json \
           file://mycroft-setup.service \
           file://mycroft.service \
          "

S = "${WORKDIR}/git"

inherit systemd

# Mycroft installs itself on the host
# Just copy the setup files to the rootfs
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

FILES_${PN} += "${libdir}/mycroft"

RDEPENDS_${PN} = "python3"

# Install as many Python packages as we can.
# We don't yet have all the packages in meta-python.
# Install as many as we can and we will install the rest on the target with pip.
# TODO: Add all the remaining packages and remove pip
RDEPENDS_${PN} += "python3-pip \
                   python3-requests python3-pillow \
                   python3-tornado python3-pyyaml \
                   python3-pyalsaaudio python3-inflection \
                   python3-pyserial python3-psutil \
                   python3-pyaudio python3-fann2 \
                   python3-pocketsphinx \
                   python3-xxhash \
                 "

# Mycroft uses Alsa, PulseAudio and Flac
RDEPENDS_${PN} += "alsa-utils alsa-plugins alsa-tools"
RDEPENDS_${PN} += "pulseaudio pulseaudio-misc pulseaudio-server"
RDEPENDS_${PN} += "flac"

# Mycroft can do this itself on the target, but it's quicker to do it here
RDEPENDS_${PN} += "mimic"

SYSTEMD_SERVICE_${PN} = "mycroft-setup.service mycroft.service"
