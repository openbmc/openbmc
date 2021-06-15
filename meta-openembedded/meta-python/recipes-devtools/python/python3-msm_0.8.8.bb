SUMMARY = "Mycroft Skill Manager, in python!"
HOMEPAGE = "https://github.com/MycroftAI/mycroft-skills-manager"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e23fadd6ceef8c618fc1c65191d846fa"

SRC_URI[md5sum] = "cf1fc0d1d86af68003cae53c71ec6288"
SRC_URI[sha256sum] = "a502aee54917cd394217b31c977a1ba3d9541a0120e0a045c49fd77b328e4a29"

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
