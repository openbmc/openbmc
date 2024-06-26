SUMMARY = "Compiles a set of authorized_keys files into a single file"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

SRC_URI += " \
  file://authorized-keys-comp.service \
  file://authorized-keys-comp.sh \
  "

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

RDEPENDS:${PN} += "bash"

SYSTEMD_SERVICE:${PN} += "authorized-keys-comp.service"

FILES:${PN} += "/home"

AUTHORIZED_KEYS_COMP_USERS ?= "root"

do_install:append() {
  install -d -m0755 ${D}${libexecdir}
  install -m0755 authorized-keys-comp.sh ${D}${libexecdir}/

  install -d -m0755 ${D}${systemd_system_unitdir}
  install -m0644 authorized-keys-comp.service ${D}${systemd_system_unitdir}/

  for user in ${AUTHORIZED_KEYS_COMP_USERS}; do
    install -d -m0755 ${D}/home/$user/.ssh
    ln -sv /run/authorized_keys/$user ${D}/home/$user/.ssh/authorized_keys
  done
}
