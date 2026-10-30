SUMMARY = "Google USB EEM Gadget Configuration Script"
DESCRIPTION = "Google USB EEM Gadget Configuration Script"
PR = "r1"
PV = "0.2"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend = "${THISDIR}/${PN}:"

inherit systemd

DEPENDS += "m4-native"
DEPENDS += "systemd"
RDEPENDS:${PN} += "bash"
RDEPENDS:${PN} += "gbmc-net-common"

BMC_USB_ECM_PRODUCT_ID ??= ""
BMC_USB_ECM_PRODUCT_NAME ??= "${MACHINE} BMC"
BMC_USB_ECM_HOST_MAC ??= "invalid"
BMC_USB_ECM_DEV_MAC ??= "invalid"
BMC_USB_ECM_BIND_DEV ??= ""
BMC_USB_TYPE ??= "eem"
BMC_USB_IFACE ??= "gusb0"

# Statically configured gadgets, each created early at boot by its own
# google-usb-static@<iface>.service, without waiting on entity-manager.
# They are attached once systemd-networkd is running, which brings their
# links up.
# BMC_USB_GADGETS is a space separated list of interface names. Each one is
# described by BMC_USB_GADGET_<iface>, a ';' separated list of key=value pairs:
#   product-id    USB product id (required)
#   bind-device   UDC to bind, as listed in /sys/class/udc/ (required)
#   product-name  Product string, default: "${MACHINE} BMC"
#   dev-type      Gadget function type, default: "eem"
#   host-mac      MAC address of the host side
#   host-mac-idx  Index into the gbmc-mac-config list to use as the host MAC
#   dev-mac       MAC address of the device (gadget) side
#   iface-alias   Alias of the network interface
#   no-bridge     Set to 1 to keep the link out of the default bridge
#   condition-path  Only create the gadget if this path exists, e.g. the
#                 l2br netdev in the images that have one
# Statically configured interfaces are named gusb<n>, like BMC_USB_IFACE.
# gusbem<n> is what google-usb-dynamic names those from entity-manager.
# e.g.
#   BMC_USB_GADGETS = "gusb0"
#   BMC_USB_GADGET_gusb0 = "product-id=0x022b;bind-device=f0835000.udc;product-name=Foo BMC Bridge"
BMC_USB_GADGETS ??= ""
# Whether the static gadgets must stay down in bare metal mode, where the host
# must not reach the BMC. Machines with a bare metal mode have to set this:
#   ""        No bare metal mode (default), the gadgets are always created
#   "flag"    Bare metal mode is enabled by BMC_USB_GADGETS_BM_FILE
#   "always"  The image is always in bare metal mode
# The check is a unit condition, so the gadgets don't wait for the bare metal
# setup services.
BMC_USB_GADGETS_BM ??= ""
BMC_USB_GADGETS_BM_FILE ??= "/var/google/config-package/enable-bm.flag"
BMC_USB_GADGET_KEYS = "product-id product-name bind-device dev-type host-mac host-mac-idx dev-mac iface-alias no-bridge condition-path"
# The gadget configuration is machine specific
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI += "file://usb_network.service.m4"
SRC_URI += "file://usb_network.sh"
SRC_URI += "file://google-usb-static@.service"
SRC_URI += "file://google-usb-static.target"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "${@'usb_network.service' if d.getVar('BMC_USB_ECM_PRODUCT_ID') else ''}"
SYSTEMD_SERVICE:${PN} += "${@' '.join(['google-usb-static.target'] + ['google-usb-static@%s.service' % i for i in d.getVar('BMC_USB_GADGETS').split()]) if d.getVar('BMC_USB_GADGETS').split() else ''}"
FILES:${PN} += "${systemd_system_unitdir}/google-usb-static@.service"
FILES:${PN} += "${systemd_system_unitdir}/google-usb-static@*.service.d"

python google_usb_static_gen() {
    import os
    import re

    gadgets = d.getVar('BMC_USB_GADGETS').split()
    if not gadgets:
        return
    keys = d.getVar('BMC_USB_GADGET_KEYS').split()
    outdir = os.path.join(d.getVar('UNPACKDIR'), 'google-usb-static')
    bb.utils.remove(outdir, recurse=True)
    bb.utils.mkdirhier(outdir)

    # Applies to every instance, see BMC_USB_GADGETS_BM
    bm = d.getVar('BMC_USB_GADGETS_BM')
    bm_file = d.getVar('BMC_USB_GADGETS_BM_FILE')
    if bm == 'flag':
        if not bm_file.startswith('/') or re.search(r'\s', bm_file):
            bb.fatal('Invalid BMC_USB_GADGETS_BM_FILE: %s' % bm_file)
        cond = 'ConditionPathExists=!%s' % bm_file
    elif bm == 'always':
        cond = 'ConditionPathExists=/dev/null/bare-metal'
    elif bm:
        bb.fatal('Invalid BMC_USB_GADGETS_BM: %s' % bm)
    if bm:
        dropin = os.path.join(outdir, 'google-usb-static@.service.d')
        bb.utils.mkdirhier(dropin)
        with open(os.path.join(dropin, 'bare-metal.conf'), 'w') as f:
            f.write('[Unit]\n')
            f.write('# The host must not reach the BMC in bare metal mode\n')
            f.write(cond + '\n')
    for iface in gadgets:
        if not re.fullmatch(r'[a-zA-Z0-9._-]+', iface):
            bb.fatal('Invalid BMC_USB_GADGETS interface name: %s' % iface)
        spec = d.getVar('BMC_USB_GADGET_' + iface)
        if not spec:
            bb.fatal('Please define BMC_USB_GADGET_%s' % iface)
        cfg = {
            'product-name': '%s BMC' % d.getVar('MACHINE'),
            'dev-type': 'eem',
            'no-bridge': '0',
        }
        for item in spec.split(';'):
            if not item.strip():
                continue
            key, sep, val = item.partition('=')
            key = key.strip()
            if not sep or key not in keys:
                bb.fatal('BMC_USB_GADGET_%s: invalid entry "%s"' % (iface, item))
            if re.search(r'["\\\n$`]', val):
                bb.fatal('BMC_USB_GADGET_%s: invalid value for %s' % (iface, key))
            cfg[key] = val.strip()
        for key in ('product-id', 'bind-device'):
            if not cfg.get(key):
                bb.fatal('BMC_USB_GADGET_%s: missing %s' % (iface, key))
        cond = cfg.pop('condition-path', '')
        if cond and (not cond.startswith('/') or re.search(r'\s', cond)):
            bb.fatal('BMC_USB_GADGET_%s: invalid condition-path %s' % (iface, cond))
        with open(os.path.join(outdir, iface + '.conf'), 'w') as f:
            for key in keys:
                if key == 'condition-path':
                    continue
                f.write('%s="%s"\n' % (key.upper().replace('-', '_'), cfg.get(key, '')))
        dropin = os.path.join(outdir, 'google-usb-static@%s.service.d' % iface)
        if cfg.get('host-mac-idx'):
            bb.utils.mkdirhier(dropin)
            with open(os.path.join(dropin, 'mac-idx.conf'), 'w') as f:
                f.write('[Unit]\n')
                f.write('Wants=gbmc-mac-config.service\n')
                f.write('After=gbmc-mac-config.service\n')
        if cond:
            bb.utils.mkdirhier(dropin)
            with open(os.path.join(dropin, 'condition.conf'), 'w') as f:
                f.write('[Unit]\n')
                f.write('ConditionPathExists=%s\n' % cond)
}
google_usb_static_gen[vardeps] += "BMC_USB_GADGETS_BM BMC_USB_GADGETS_BM_FILE ${@' '.join('BMC_USB_GADGET_' + i for i in d.getVar('BMC_USB_GADGETS').split())}"
do_compile[postfuncs] += "google_usb_static_gen"

do_compile:append() {
  if [ -n "${BMC_USB_ECM_PRODUCT_ID}" ]; then
    test "X${BMC_USB_ECM_PRODUCT_NAME}" != "X" || bberror "Please define BMC_USB_ECM_PRODUCT_NAME"
    test "X${BMC_USB_ECM_BIND_DEV}" != "X" || bberror "Please define BMC_USB_ECM_BIND_DEV"

    m4 \
        -DM_BMC_USB_PRODUCT_ID="${BMC_USB_ECM_PRODUCT_ID}" \
        -DM_BMC_USB_PRODUCT_NAME="${BMC_USB_ECM_PRODUCT_NAME}" \
        -DM_BMC_USB_TYPE="${BMC_USB_TYPE}" \
        -DM_BMC_USB_HOST_MAC="${BMC_USB_ECM_HOST_MAC}" \
        -DM_BMC_USB_DEV_MAC="${BMC_USB_ECM_DEV_MAC}" \
        -DM_BMC_USB_IFACE="${BMC_USB_IFACE}" \
        -DM_BMC_USB_BIND_DEV="${BMC_USB_ECM_BIND_DEV}" \
        -DM_SCRIPT_INSTALL_DIR="${bindir}" \
        ${UNPACKDIR}/usb_network.service.m4 > ${UNPACKDIR}/usb_network.service
  fi
}

do_install:append() {
  install -d ${D}/${bindir}
  install -m 0755 ${UNPACKDIR}/usb_network.sh ${D}/${bindir}

  if [ -n "${BMC_USB_ECM_PRODUCT_ID}" ]; then
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/usb_network.service ${D}${systemd_system_unitdir}
  fi

  if [ -n "${@d.getVar('BMC_USB_GADGETS').strip()}" ]; then
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/google-usb-static@.service ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/google-usb-static.target ${D}${systemd_system_unitdir}
    if [ -d ${UNPACKDIR}/google-usb-static/google-usb-static@.service.d ]; then
      install -d ${D}${systemd_system_unitdir}/google-usb-static@.service.d
      install -m 0644 ${UNPACKDIR}/google-usb-static/google-usb-static@.service.d/* \
        ${D}${systemd_system_unitdir}/google-usb-static@.service.d/
    fi
    install -d ${D}${datadir}/${BPN}
    for iface in ${BMC_USB_GADGETS}; do
      install -m 0644 ${UNPACKDIR}/google-usb-static/$iface.conf ${D}${datadir}/${BPN}/
      dropin=google-usb-static@$iface.service.d
      if [ -d ${UNPACKDIR}/google-usb-static/$dropin ]; then
        install -d ${D}${systemd_system_unitdir}/$dropin
        install -m 0644 ${UNPACKDIR}/google-usb-static/$dropin/* \
          ${D}${systemd_system_unitdir}/$dropin/
      fi
    done
  fi
}
