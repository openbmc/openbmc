SUMMARY = "Mycroft Skill Manager, in python!"
HOMEPAGE = "https://github.com/MycroftAI/mycroft-skills-manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[md5sum] = "d5f580c58389b337f5577cb92f36e788"
SRC_URI[sha256sum] = "c201785997f3b766ec376a89bbb3367889ac542183ca26733ffe002bb94917b4"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    python3-pako \
    python3-monotonic \
    python3-appdirs \
"

do_install_append() {
    # Stop this from being installed
    rm -rf ${D}/usr/share
}
