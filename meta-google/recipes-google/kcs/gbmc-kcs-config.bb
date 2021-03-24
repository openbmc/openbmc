SUMMARY = "Configures KCS for a gBMC system"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

S = "${WORKDIR}"

PROVIDES += "virtual-obmc-host-ipmi-hw"
RPROVIDES_${PN} += "virtual-obmc-host-ipmi-hw"

FILES_${PN} += "${systemd_system_unitdir}"
RDEPENDS_${PN} += "google-kcsbridge"

GBMC_KCS_DEV ?= ""

def systemd_escape_char(c):
  return '\\x{:x}'.format(ord(c))

def systemd_escape(unit):
  import string
  ret = ''
  if len(unit) > 0 and unit[0] == '.':
    ret += systemd_escape_char(unit[0])
    unit = unit[1:]
  for c in unit:
    if c == '/':
      ret += '-'
    elif c not in {*string.ascii_letters, *string.digits, ':', '_', '.'}:
      ret += systemd_escape_char(c)
    else:
      ret += c
  return ret

do_install_append() {
  if [ -z '${GBMC_KCS_DEV}' ]; then
    echo "Missing GBMC_KCS_DEV" >&2
    exit 1
  fi

  wantdir=${D}${systemd_system_unitdir}/multi-user.target.wants
  install -d -m0755 $wantdir
  inst="${@systemd_escape(GBMC_KCS_DEV)}"
  ln -sv ../kcsbridge@.service $wantdir/kcsbridge@$inst.service
}
