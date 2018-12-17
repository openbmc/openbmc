SUMMARY = "Additional utilities for the opkg package manager"
SUMMARY_update-alternatives-opkg = "Utility for managing the alternatives system"
SECTION = "base"
HOMEPAGE = "http://git.yoctoproject.org/cgit/cgit.cgi/opkg-utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://opkg.py;beginline=2;endline=18;md5=63ce9e6bcc445181cd9e4baf4b4ccc35"
PROVIDES += "${@bb.utils.contains('PACKAGECONFIG', 'update-alternatives', 'virtual/update-alternatives', '', d)}"

SRC_URI = "http://git.yoctoproject.org/cgit/cgit.cgi/${BPN}/snapshot/${BPN}-${PV}.tar.gz \
           file://0001-Switch-all-scripts-to-use-Python-3.x.patch \
           file://0001-Only-use-sort-name-on-versions-of-tar-which-support-.patch \
           file://0002-opkg-build-Use-local-time-for-build_date-since-opkg-.patch \
           file://threaded-xz.patch \
           file://pigz.patch \
"
SRC_URI_append_class-native = " file://tar_ignore_error.patch"
UPSTREAM_CHECK_URI = "http://git.yoctoproject.org/cgit/cgit.cgi/opkg-utils/refs/"


SRC_URI[md5sum] = "391c1c80aca26961375dd22e1d2d0cbf"
SRC_URI[sha256sum] = "26b3f4dd1c1ef65f519d4cddfb35ebea41ae8af989aea2699a39783598c33769"

TARGET_CC_ARCH += "${LDFLAGS}"

# For native builds we use the host Python
PYTHONRDEPS = "python3 python3-shell python3-io python3-math python3-crypt python3-logging python3-fcntl python3-pickle python3-compression python3-stringold"
PYTHONRDEPS_class-native = ""

PACKAGECONFIG = "python update-alternatives"
PACKAGECONFIG[python] = ",,,${PYTHONRDEPS}"
PACKAGECONFIG[update-alternatives] = ",,,"

do_install() {
	oe_runmake PREFIX=${prefix} DESTDIR=${D} install
	if ! ${@bb.utils.contains('PACKAGECONFIG', 'update-alternatives', 'true', 'false', d)}; then
		rm -f "${D}${bindir}/update-alternatives"
	fi

    if ! ${@bb.utils.contains('PACKAGECONFIG', 'python', 'true', 'false', d)}; then
        grep -lZ "/usr/bin/env.*python" ${D}${bindir}/* | xargs -0 rm
    fi
}

do_install_append_class-target() {
	if [ -e "${D}${bindir}/update-alternatives" ]; then
		sed -i ${D}${bindir}/update-alternatives -e 's,/usr/bin,${bindir},g; s,/usr/lib,${nonarch_libdir},g'
	fi
}

# These are empty and will pull python3-dev into images where it wouldn't
# have been otherwise, so don't generate them.
PACKAGES_remove = "${PN}-dev ${PN}-staticdev"

PACKAGES =+ "update-alternatives-opkg"
FILES_update-alternatives-opkg = "${bindir}/update-alternatives"
RPROVIDES_update-alternatives-opkg = "update-alternatives update-alternatives-cworth"
RREPLACES_update-alternatives-opkg = "update-alternatives-cworth"
RCONFLICTS_update-alternatives-opkg = "update-alternatives-cworth"

pkg_postrm_update-alternatives-opkg() {
	rm -rf $D${nonarch_libdir}/opkg/alternatives
	rmdir $D${nonarch_libdir}/opkg || true
}

BBCLASSEXTEND = "native nativesdk"

CLEANBROKEN = "1"
