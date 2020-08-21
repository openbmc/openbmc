SUMMARY = "Linux libcamera framework"
SECTION = "libs"

LICENSE = "GPL-2.0+ & LGPL-2.1+"

LIC_FILES_CHKSUM = "\
    file://LICENSES/GPL-2.0-or-later.txt;md5=fed54355545ffd980b814dab4a3b312c \
    file://LICENSES/LGPL-2.1-or-later.txt;md5=2a4f4fd2128ea2f65047ee63fbca9f68 \
"

SRC_URI = " \
        git://linuxtv.org/libcamera.git;protocol=git \
"

SRCREV = "5f2f9406cebc668f0d69007d1ea59ef3c56ef28c"

PV = "202006+git${SRCPV}"

S = "${WORKDIR}/git"

DEPENDS = "python3-pyyaml-native udev gnutls boost chrpath-native"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'qt', 'qtbase qtbase-native', '', d)}"

RDEPENDS_${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland qt', 'qtwayland', '', d)}"

inherit meson pkgconfig python3native

do_install_append() {
    chrpath -d ${D}${libdir}/libcamera.so
}

addtask do_recalculate_ipa_signatures_package after do_package before do_packagedata
do_recalculate_ipa_signatures_package() {
    local modules
    for module in $(find ${PKGD}/usr/lib/libcamera -name "*.so.sign"); do
        module="${module%.sign}"
        if [ -f "${module}" ] ; then
            modules="${modules} ${module}"
        fi
    done

    ${S}/src/ipa/ipa-sign-install.sh ${B}/src/ipa-priv-key.pem "${modules}"
}

FILES_${PN}-dev = "${includedir} ${libdir}/pkgconfig"
FILES_${PN} += " ${libdir}/libcamera.so"

