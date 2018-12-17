SUMMARY = "XCB: The X protocol C binding headers"
DESCRIPTION = "Function prototypes for the X protocol C-language Binding \
(XCB).  XCB is a replacement for Xlib featuring a small footprint, \
latency hiding, direct access to the protocol, improved threading \
support, and extensibility."
HOMEPAGE = "http://xcb.freedesktop.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=XCB"

SECTION = "x11/libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d763b081cb10c223435b01e00dc0aba7 \
                    file://src/dri2.xml;beginline=2;endline=28;md5=f8763b13ff432e8597e0d610cf598e65"

SRC_URI = "http://xcb.freedesktop.org/dist/${BP}.tar.bz2"

SRC_URI[md5sum] = "abe9aa4886138150bbc04ae4f29b90e3"
SRC_URI[sha256sum] = "7b98721e669be80284e9bbfeab02d2d0d54cd11172b72271e47a2fe875e2bde1"

inherit autotools pkgconfig

# Force the use of Python 3 and a specific library path so we don't need to
# depend on python3-native
CACHED_CONFIGUREVARS += "PYTHON=python3 am_cv_python_pythondir=${libdir}/xcb-proto"

PACKAGES += "python-xcbgen"

FILES_${PN} = ""
FILES_${PN}-dev += "${datadir}/xcb/*.xml ${datadir}/xcb/*.xsd"
FILES_python-xcbgen = "${libdir}/xcb-proto"

RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"

# Need to do this dance because we're forcing the use of host Python above and
# if xcb-proto is built with Py3.5 and then re-used from sstate on a host with
# Py3.6 the second build will write new cache files into the sysroot which won't
# be listed in the manifest so won't be deleted, resulting in an error on
# rebuilds.  Solve this by deleting the entire cache directory when this package
# is removed from the sysroot.
SSTATEPOSTINSTFUNCS += "xcb_sstate_postinst"
xcb_sstate_postinst() {
	if [ "${BB_CURRENTTASK}" = "populate_sysroot" -o "${BB_CURRENTTASK}" = "populate_sysroot_setscene" ]
	then
		cat <<EOF >${SSTATE_INST_POSTRM}
#!/bin/sh
rm -rf ${libdir}/xcb-proto/xcbgen/__pycache__
EOF
	fi
}
