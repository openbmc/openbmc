SUMMARY = "Mycroft Skill Manager, in python!"
HOMEPAGE = "https://github.com/MycroftAI/mycroft-skills-manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[md5sum] = "16f755ea554c332cdb666dfc1109f7f2"
SRC_URI[sha256sum] = "9878eecbf7255d4907637700ecfeeacb9fe586409ee3ae05d406683ad18d7e5e"

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
