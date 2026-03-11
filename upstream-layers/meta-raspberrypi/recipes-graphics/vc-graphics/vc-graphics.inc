DESCRIPTION = "Graphics libraries for BCM2835."
LICENSE = "Broadcom-RPi"

LIC_FILES_CHKSUM = "file://LICENCE;md5=86e53f5f5909ee66900418028de11780"

PROVIDES = "virtual/libgles2 virtual/egl"
COMPATIBLE_MACHINE = "^rpi$"

INHIBIT_DEFAULT_DEPS = "1"

include recipes-bsp/common/raspberrypi-firmware.inc

SRC_URI += " \
    file://egl.pc \
    file://vchiq.sh \
"

S = "${RPIFW_S}/${VCDIR}"

INCPR = "r1"

inherit pkgconfig update-rc.d

do_install () {
  install -d ${D}${bindir}
  cp -R bin/* ${D}${bindir}

  install -d ${D}${libdir}
  # note: -H option to deref symlinked .so
  cp -R -H lib/* ${D}${libdir}

  install -d ${D}${includedir}
  cp -R include/* ${D}${includedir}

  install -d ${D}${libdir}/pkgconfig
  install -m 0644 ${UNPACKDIR}/egl.pc ${D}${libdir}/pkgconfig/

  install -d ${D}/${sysconfdir}/init.d
  install -m 0755 ${UNPACKDIR}/vchiq.sh ${D}${sysconfdir}/init.d/
}

# These are proprietary binaries generated elsewhere so don't check ldflags
INSANE_SKIP:${PN} = "ldflags"

INITSCRIPT_NAME = "vchiq.sh"
INITSCRIPT_PARAMS = "start 03 S ."

FILES:${PN} = "${bindir}/* \
               ${libdir}/lib*.so \
               ${sysconfdir}/init.d \
               ${libdir}/plugins"
FILES:${PN}-dev = "${libdir}/pkgconfig \
                   ${includedir}"
FILES:${PN}-dbg += "${libdir}/plugins/.debug"
