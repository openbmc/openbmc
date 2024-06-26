SUMMARY = "Google DHCP completion daemon"
DESCRIPTION = "Google DHCP completion daemon"
GOOGLE_MISC_PROJ = "dhcp-done"

require ../google-misc/google-misc.inc

EXTRA_OEMESON = " \
        -Dtests=disabled \
        "
inherit systemd

SYSTEMD_SERVICE:${PN} += "dhcp-done.service"

DEPENDS += " \
  sdeventplus \
  stdplus \
  "

SRC_URI += "file://50-dhcp-done.rules"
FILES:${PN} += "${sysconfdir}/nftables"
do_install:append() {
  nftables_dir=${D}${sysconfdir}/nftables
  install -d -m0755 "$nftables_dir"
  install -m0644 ${UNPACKDIR}/50-dhcp-done.rules $nftables_dir/
}
